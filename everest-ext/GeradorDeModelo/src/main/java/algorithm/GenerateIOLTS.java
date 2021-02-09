package algorithm;

import gui.GenerateTab;
import model.Automaton_;
import model.IOLTS;
import model.State_;
import model.Transition_;
import parser.ExportAutFile;
import util.Settings;

import javax.swing.*;
import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static algorithm.IocoConformance.verifyIOCOConformance;
import static parser.ImportAutFile.autToIOLTS;

public class GenerateIOLTS {
    protected static GenerateTab parent;
    protected static Settings settings;
    private static List<String> unusedLabels = null;

    /**
     * Main method that executes generation of IOLTS according to settings parameters.
     */
    public static void run(GenerateTab tab, Settings s){

        List<IOLTS> ioltsList = new ArrayList<>();

        if(tab != null)
        {
            parent = tab;
        }

        if(s != null)
        {
            settings = s;
        }

        // Generation without conformance requirement
        if(settings.getConformanceType().equals("None"))
        {
            for(int i = 0; i < settings.getModelNumber(); i++)
            {
                ioltsList.add(generateIOLTS());
            }
        }
        // Generation with conformance requirement
        else if(settings.getConformanceType().equals("Conforms") && settings.getPathBase().length() > 0)
        {
            settings.setInputComplete(true);
            settings.setDeterministic(true);
            try {
                IOLTS base = autToIOLTS(settings.getPathBase(), false, null, null);
                while (ioltsList.size() < settings.getModelNumber())
                {
                    IOLTS iolts = generateConformsIOLTS(base);
                    if(iolts == null)
                    {
                        break;
                    }
                    else if(!ioltsList.contains(iolts)){
                        ioltsList.add(iolts);
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
        // Generation with non-conformance requirement
        else if(settings.getConformanceType().equals("Not Conform") && settings.getPathBase().length() > 0)
        {
            try {
                IOLTS base = autToIOLTS(settings.getPathBase(), false, null, null);

                while (ioltsList.size() < settings.getModelNumber())
                {
                    IOLTS iolts = generateNotConformsIOLTS(base);
                    if(iolts == null)
                    {
                        break;
                    }
                    else if(!ioltsList.contains(iolts)) {
                        ioltsList.add(iolts);
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        // Exports generated models to .aut converter class
        ExportAutFile.writeIOLTS(settings.getName(), settings.getPath(), (ArrayList<IOLTS>) ioltsList);
    }

    /**
     * Main generation method that executes each step of the IOLTS construction.
     * @return list of generated IOLTS models
     */
    public static IOLTS generateIOLTS(){
        // Declarations
        IOLTS iolts = new IOLTS();

        boolean PropP;
        boolean PropD;
        boolean Prop;

        if(settings.isInputComplete() || settings.isOutputComplete()) { PropP = false; }
        else { PropP = true; }

        if(settings.isInputDeterminism() || settings.isOutputDeterminism()) { PropD = false; }
        else { PropD = true; }

        Prop = PropP && PropD;

        // Set number of transitions
        settings.setTransitions();

        // Initialize states
        iolts.addAllStates(generateStates(settings.getStateNumber()));

        // Initialize given inputs and outputs
        iolts.addAllInputs(settings.getInputs());
        iolts.addAllOutputs(settings.getOutputs());

        // Initialize inputs
        iolts.addAllInputs(generateInputs(settings.getInputNumber(), iolts));

        // Initialize outputs
        iolts.addAllOutputs(generateOutputs(settings.getOutputNumber(), iolts));

        // Display initialized alphabet in GUI
        if(parent != null)
        {
            parent.setLabels(iolts.getInputs(), iolts.getOutputs());
        }

        // Check if internal actions are enabled
        if(settings.isBlackBox())
        {
            iolts.getAlphabet().add("τ"); // Adds tau
        }

        // Sets initial state as reachable state
        iolts.setInitialState(iolts.getStates().get(0));
        iolts.addReachableState(iolts.getInitialState());
        iolts.resetAdjacencyLists();
        iolts.markAllNotVisited();

        // Auxiliary variables for loop
        int m = settings.getTransitionNumber();
        State_ s1 = null;
        State_ s2 = null;
        unusedLabels = new ArrayList<>();
        unusedLabels.addAll(iolts.getInputs());
        unusedLabels.addAll(iolts.getOutputs());

        // Main generation loop
        while( (!iolts.getReachableStates().containsAll(iolts.getStates())) ||
                (!Prop) ||
                (iolts.getTransitions().size() < m) ||
                (!unusedLabels.isEmpty()))
        {
            s1 = setState1(iolts, PropP);

            if(s1 == null) return iolts; // If no valid states are found for s1, finish

            s2 = setState2(iolts, s1, settings.isAcyclic());

            if(s2 == null) return iolts; // If no valid states are found for s2, finish

            if(Prop)
            {
                createTransition(s1, s2, iolts.getAlphabet(), iolts);
            }
            else
            {
                if(!PropP)
                {
                    createCompleteTransition(s1, s2, iolts);
                    PropP = checkCompleteness(iolts);
                }
                else
                {
                    createDeterministicTransition(s1, s2, iolts);
                    PropD = checkDeterminism(iolts);
                }
            }

            Prop = PropP && PropD;
        }

        // Check for non-quiescence flag
        if(settings.isNonQuiescent())
        {
            iolts.getOutputs().add("δ");

            for (State_ s : iolts.getStates()) {
                if(s.getOut().size() == 0)
                {
                    Transition_ t = new Transition_(s, "δ", s);
                    s.addTransition(t);
                    s.addOut("δ");
                }
            }
        }

        return iolts;
    }

    /**
     * Generation method for conformance requirement.
     */
    public static IOLTS generateConformsIOLTS(IOLTS base)
    {
        IOLTS model = null;
        // Controls for adjustments

            // Desired number of states
            int target_st = settings.getStateNumber() != base.getStates().size()
                    && settings.getStateNumber() > 0
                    ? settings.getStateNumber()
                    : base.getStates().size();

            // Desired number of transitions
            int target_tr = settings.getRequestedTransitionNumber() != base.getTransitions().size()
                    && settings.getRequestedTransitionNumber() > 0
                    ? settings.getRequestedTransitionNumber()
                    : base.getTransitions().size();

            // Loops passed since last changr of transitions number
            int unchanged_loops = 0;

        // Controls for loops
        boolean conforms = false;
        Random r = new Random();

        // Focuses on removing states
        // if-> requested state number is less than base model
        if(target_st < base.getStates().size())
        {
            // Copia modelo base
            model = IOLTS.copy(base);

            while(model.getStates().size() > target_st) {

                List<State_> removable = new ArrayList<>();

                // Obtém estados a serem removidos
                while(removable.size() < base.getStates().size() - target_st)
                {
                    // Escolhe algum estado aleatório
                    int rmi = r.nextInt(model.getStates().size());
                    State_ s = model.getStates().get(rmi);
                    if(!removable.contains(s))
                    {
                        removable.add(s);
                    }
                }

                // Verifica se estados escolhidos causam quiescência indevida
                for(State_ s : model.getStates())
                {
                    // Verifica se estado tem algum output
                    if(!removable.contains(s) && s.getOut().size() > 0)
                    {
                        List<Transition_> transition_list = s.getOutputTransitions(model.getOutputs());
                        List<Transition_> safe = new ArrayList<>();
                        for(Transition_ t : transition_list)
                        {
                            if(!removable.contains(t.getEndState()))
                            {
                               safe.add(t);
                            }
                        }

                        // Se não sobrou nenhuma transição de output
                        if(safe.size() == 0)
                        {
                            // Seleciona estado aleatório da adjacência para remover de removables
                            boolean done = false;
                            do {
                                int rmi = r.nextInt(s.getAdj().size());
                                State_ random = s.getAdj().get(rmi);
                                if(removable.contains(random))
                                {
                                    removable.remove(random);
                                    done = true;
                                }
                            } while(!done);
                        }
                    }
                }

                // Verifica se estados escolhidos mantém alcançabilidade
                for(State_ rs : removable)
                {
                    State_ problematic = null;

                    // Obtém estados alcançados por s
                    List<State_> adj = rs.getAdj();

                    for(State_ a : adj)
                    {
                        // Obtém outros estados que alcançam cada adjacência
                        List<State_> a_adj = model.checkReachable(a);

                        // Remove os estados que serão removidos
                        a_adj.removeAll(removable);

                        // Verifica se sobraram estados
                        // e, se não, retira da lista de removíveis
                        if(a_adj.size() == 0)
                        {
                            problematic = rs;
                            break;
                        }

                    }

                    // Se estado deu problema, remove ele
                    if(problematic != null)
                    {
                        removable.remove(problematic);
                        break;
                    }
                }

                // Se tiver os estados necessários, remove do modelo
                if(removable.size() == base.getStates().size() - target_st)
                {
                    for(State_ rs : removable)
                    {
                        // Remove o estado do modelo
                        model.getStates().remove(rs);
                        model.getReachableStates().remove(rs);

                        // Remove transições envolvendo o estado
                        for(State_ s : model.getStates())
                        {
                            List<Transition_> rst = new ArrayList<>();
                            for(Transition_ t : s.getTransitions())
                            {
                                if(t.getEndState().equals(rs))
                                {
                                    // Adiciona à lista das transições pra remover
                                    rst.add(t);
                                }
                            }
                            s.getTransitions().removeAll(rst);

                            // Remove da adjacência do estado
                            s.getAdj().remove(rs);

                            // Remove das transições do modelo
                            model.getTransitions().removeAll(rst);
                        }
                    }

                }

                // Ajustes de parâmetros
                if(removable.size() >= target_st)
                {
                    unchanged_loops++;
                }
                else {
                    unchanged_loops = 0;
                }

                if(unchanged_loops > 10) {
                    target_st++;
                    unchanged_loops = 0;

                    if(target_st == base.getTransitions().size()) {
                        return null;
                    }
                }
            }
        }

        // Focuses on removing transitions
        // if-> requested transition number is less than base model
        // AND target number of states is equal to base model
        if((target_tr < base.getTransitions().size())
            && (target_st == base.getStates().size()))
        {
            int last_size = base.getTransitions().size(); // Nº of transitions of last conform model
            while(!conforms) {
                // Copia modelo base
                model = IOLTS.copy(base);

                while(model.getTransitions().size() > target_tr)
                {
                    // Escolhe alguma transição aleatória entre as transições de output
                    // (não pode remover input porque gerado tem que ser IE)
                    int rmi = r.nextInt(model.getOutputTransitions().size());

                    // Remove da lista de transições
                    Transition_ t = model.getOutputTransitions().remove(rmi);

                    // Remove dos estados envolvidos
                    t.getIniState().getTransitions().remove(t);
                    t.getEndState().getTransitions().remove(t);
                    model.getTransitions().remove(t);
                }

                // Verificação de conformidade
                if(doesConform(base, model))
                {
                    conforms = true;
                }
                else {
                    // Ajuste dos parâmetros
                    if(last_size == model.getTransitions().size())
                    {
                        unchanged_loops++;
                    }
                    else {
                        last_size = model.getTransitions().size();
                        unchanged_loops = 0;
                    }

                    // Verifica se precisa ajustar número alvo de transições
                    if(unchanged_loops > 10) {
                        target_tr++;
                        if(target_tr == base.getTransitions().size()) {
                            model = null;
                            break;
                        }
                    }
                }

            }
        }

        // Focuses on adding states
        // if-> requested state number is more than base model
        if(target_st > base.getStates().size())
        {
            unchanged_loops = 0;
            int last_size = base.getStates().size(); // Nº of states of last conform model
            boolean modified = false;

            do {
                // Copia modelo base
                model = IOLTS.copy(base);

                // Cria novos estados
                List<State_> S = new ArrayList<>();
                for(int i = base.getStates().size(); i < target_st; i++)
                {
                    State_ s = new State_("s" + i);
                    S.add(s);
                }

                // Para cada estado criado
                for (State_ sl : S)
                {
                    // Verifica se existe estado com |incoming(s)| > 1
                    List<State_> unchecked = new ArrayList<>();
                    unchecked.addAll(model.getStates());

                    do {
                        // Estado aleatório s
                        int rmi = r.nextInt(unchecked.size());
                        State_ s = unchecked.get(rmi);

                        List<Transition_> in = model.incoming(s);

                        // Se encontrou um apropriado
                        if(in.size() > 1)
                        {
                            // Seleciona transição aleatória do conjunto
                            rmi = r.nextInt(in.size());
                            Transition_ t = in.get(rmi);

                            // Substitui estado alvo pelo novo estado
                            t.setEndState(sl);

                            List<Transition_> t_outs = new ArrayList<>();
                            for (Transition_ ti : s.getTransitions())
                            {
                                // Copia transições de input do estado original para o novo
                                if(model.getInputs().contains(ti.getLabel()))
                                {
                                    Transition_ ti_n = new Transition_(sl, ti.getLabel(), ti.getEndState());
                                    sl.addTransition(ti_n);
                                    sl.addIn(ti_n.getLabel());
                                    sl.addAdj(ti_n.getEndState());
                                    model.addTransition(ti_n);
                                }
                                // Contagem de transições de output partindo do estado original
                                else if(model.getOutputs().contains(ti.getLabel()))
                                {
                                    t_outs.add(ti);
                                }
                            }

                            // Copia número aleatório de transições de output do estado original para o novo
                            // (se tiver alguma)
                            if(t_outs.size() > 0)
                            {
                                int rout = r.nextInt(t_outs.size()) + 1; // Mínimo de 1 transição a copiar (evita quiescência)
                                for(int i = 0; i < rout; i++)
                                {
                                    // Seleciona transição aleatória
                                    rmi = r.nextInt(t_outs.size());
                                    Transition_ to = t_outs.get(rmi);

                                    // Cria cópia
                                    Transition_ to_n = new Transition_(sl, to.getLabel(), to.getEndState());
                                    sl.addTransition(to_n);
                                    sl.addIn(to_n.getLabel());
                                    sl.addAdj(to_n.getEndState());
                                    model.addTransition(to_n);
                                }
                            }

                            // Se encontrou algum pode encerrar o loop de verificação de |incoming(s)|>1
                            // e marca flag de modificação como verdadeira
                            modified = true;
                            break;
                        }

                        // Remove da lista de unchecked
                        unchecked.remove(s);

                    } while(!modified && !unchecked.isEmpty());

                }

                // Adiciona novos estados
                if(!S.isEmpty() && modified)
                {
                    model.addAllStates(S);
                }
                // Se não houve modificação
                else{
                    // Ajustes de parâmetros caso as modificações não sejam possíveis
                    if(unchanged_loops <= 10)
                    {
                        target_st--;
                        unchanged_loops++;
                    }
                    else {
                        return model = null;
                    }

                }

            } while(target_st > model.getStates().size());

        }

        // Focuses on adding transitions
        // if-> requested transition number is more than base model
        // AND target number of states is equal to base model
        if((target_tr > base.getTransitions().size())
            && (target_st == base.getStates().size()))
        {

        }

        return model;
    }

    /**
     * Generation method for non-conformance requirement.
     */
    public static IOLTS generateNotConformsIOLTS(IOLTS base)
    {
        IOLTS model = null;
        // Controls for adjustments

        // Desired number of states
        int target_st = settings.getStateNumber() != base.getStates().size()
                && settings.getStateNumber() > 0
                ? settings.getStateNumber()
                : base.getStates().size();

        // Desired number of transitions
        int target_tr = settings.getRequestedTransitionNumber() != base.getTransitions().size()
                && settings.getRequestedTransitionNumber() > 0
                ? settings.getRequestedTransitionNumber()
                : base.getTransitions().size();

        // Loops passed since last changr of transitions number
        int unchanged_loops = 0;

        // Controls for loops
        boolean conforms = false;
        Random r = new Random();

        // Focuses on adding states
        // if-> requested state number is more than base model
        if(target_st > base.getStates().size())
        {
            unchanged_loops = 0;
            int last_size = base.getStates().size(); // Nº of states of last non-conform model
            boolean modified = false;

            do {
                // Copia modelo base
                model = IOLTS.copy(base);

                // Cria novos estados
                List<State_> S = new ArrayList<>();
                for(int i = base.getStates().size(); i < target_st; i++)
                {
                    State_ s = new State_("s" + i);
                    S.add(s);
                }

                // Controladores de alterações
                boolean transicoes_incoming = false;
                boolean transicoes_outgoing = false;

                // Para cada estado criado
                for (State_ sl : S)
                {
                    // Controladores de loop
                    int iterations = 0;
                    boolean done = false;

                    // Transições incoming (só de output)
                    while(!done && iterations < 10)
                    {
                        // Verifica se tem estados disponíveis
                        List<State_> available = new ArrayList<>();
                        for (State_ os : model.getStates())
                        {
                            if(!os.getOut().containsAll(model.getOutputs()))
                            {
                                available.add(os);
                            }
                        }

                        // Se tem estados disponíveis
                        if(available.size() > 0)
                        {
                            int rmi = r.nextInt(available.size());
                            State_ origin = available.get(rmi);

                            // Label aleatória (mantendo determinismo)
                            List<String> availabels = new ArrayList<>();
                            for(String l : model.getOutputs())
                            {
                                if(!origin.getOut().contains(l))
                                {
                                    availabels.add(l);
                                }
                            }

                            if(availabels.size() > 0)
                            {
                                rmi = r.nextInt(availabels.size());
                                String label = availabels.get(rmi);

                                // Transição
                                Transition_ t1 = new Transition_(origin, label, sl);
                                origin.addTransition(t1);
                                model.addTransition(t1);

                                origin.addAdj(sl);
                                if(model.getInputs().contains(label))
                                {
                                    origin.addIn(label);
                                }
                                else {
                                    origin.addOut(label);
                                }

                                transicoes_incoming = true;
                            }

                        }

                        // Verificador aleatório se vai parar ou não
                        iterations++;
                        done = r.nextBoolean();
                    }

                    iterations = 0;
                    done = false;

                    // Transições outgoing (só de input -- até input-enabled)
                    for(String l : model.getInputs())
                    {
                        // Escolhe estado aleatório
                        int rmi = r.nextInt(model.getStates().size());
                        State_ target = model.getStates().get(rmi);

                        // Cria transição
                        Transition_ t2 = new Transition_(sl, l, target);
                        sl.addTransition(t2);
                        model.addTransition(t2);

                        sl.addAdj(sl);
                        sl.addIn(l);

                        transicoes_outgoing = true;
                    }

                    // Transições outgoing (só de output)
                    while(transicoes_incoming && !done && iterations < 10)
                    {
                        // 1. Estado aleatório de destino
                        int rmi = r.nextInt(model.getStates().size());
                        State_ target = model.getStates().get(rmi);

                        // 2. Label aleatória (mantendo determinismo)
                        List<String> availabels = new ArrayList<>();
                        for(String l : model.getOutputs())
                        {
                            if(!sl.getOut().contains(l))
                            {
                                availabels.add(l);
                            }
                        }

                        if(availabels.size() > 0)
                        {
                            rmi = r.nextInt(availabels.size());
                            String label = availabels.get(rmi);

                            // 3. Transição
                            Transition_ t3 = new Transition_(sl, label, target);
                            sl.addTransition(t3);
                            model.addTransition(t3);

                            sl.addAdj(sl);
                            sl.addOut(label);

                            transicoes_outgoing = true;
                        }

                        // Verificador aleatório se vai parar ou não
                        iterations++;
                        done = r.nextBoolean();
                    }
                }

                // Adiciona novos estados
                if(!S.isEmpty() && transicoes_incoming && transicoes_outgoing)
                {
                    model.addAllStates(S);
                }
                // Se não houve modificação
                else{
                    // Ajustes de parâmetros caso as modificações não sejam possíveis
                    if(unchanged_loops <= 10)
                    {
                        target_st--;
                        unchanged_loops++;
                    }
                    else {
                        return model = null;
                    }

                }

            } while(target_st > model.getStates().size());
        }

        return model;
    }

    /**
     * Method to verify conformance of generated model
     */
    public static boolean doesConform(IOLTS base, IOLTS model)
    {
        IOLTS iut = IOLTS.copy(model);
        iut.addQuiescentTransitions();

        Automaton_ conformidade = verifyIOCOConformance(base, iut);
        String failPath = Operations.path(base, iut, conformidade, true, false, 0);

        boolean res = failPath == "";
        return res;
    }

    /**
     * Method for generating n states
     * @param n desired number of states
     * @return list containing generated states
     */
    public static List<State_> generateStates(int n) {
        List<State_> states = new ArrayList<>();

        for(int i = 0; i < n; i++)
        {
            State_ s = new State_("s" + i);
            states.add(s);
        }

        return states;
    }

    /**
     * Method for generating input labels
     * @param x desired number of input labels
     * @return list containing generated inputs
     */
    public static List<String> generateInputs(int x, IOLTS iolts) {
        List<String> inputs = new ArrayList<>();

        int genInputs = 0;
        int j = 0;
        int suffix = 0;
        char input = 'a';

        if(settings.getInputs() != null && settings.getInputs().size() > 0)
        {
            x = x - settings.getInputs().size();
        }

        while(genInputs < x)
        {
            if(j == 13)
            {
                suffix++;
                j = 0;
            }

            String character;

            if(suffix == 0){
                character = Character.toString((char) (input + j));
            }
            else{
                character = (char) (input + j) + Integer.toString(suffix);
            }

            if(!inputs.contains(character) && !iolts.getOutputs().contains(character))
            {
                inputs.add(character);
                genInputs++;
            }

            j++;
        }

        return inputs;
    }

    /**
     * Method for generating output labels
     * @param y desired number of output labels
     * @return list containing generated inputs
     */
    public static List<String> generateOutputs(int y, IOLTS iolts) {
        List<String> outputs = new ArrayList<>();

        int genOutputs = 0;
        int j = 0;
        int suffix = 0;
        char output = 'n';

        // Check for provided input labels
        if(settings.getOutputs() != null && settings.getOutputs().size() > 0)
        {
            y = y - settings.getOutputs().size();
        }

        // Generate remaining labels if needed
        while(genOutputs < y)
        {
            if(genOutputs == 13)
            {
                suffix++;
                j = 0;
            }

            String character;

            if(suffix == 0){
                character = Character.toString((char) (output + j));
            }
            else{
                character = (char) (output + j) + Integer.toString(suffix);
            }

            if(!outputs.contains(character) && !iolts.getInputs().contains(character))
            {
                outputs.add(character);
                genOutputs++;
            }

            j++;
        }

        return outputs;
    }

    /**
     * Method for selecting a source state for a new transition
     * @param iolts Incomplete IOLTS model
     * @return selected state
     */
    public static State_ setState1(IOLTS iolts, boolean PropP) {
        State_ s1 = null;
        Random r = new Random();

        // If p=I and not all states are input-complete
        if(settings.isInputComplete() && !PropP)
        {
            List<State_> available = new ArrayList<>();

            // Finds suitable states
            for (State_ state : iolts.getReachableStates()) {
                if(!state.getIn().containsAll(iolts.getInputs()))
                {
                    available.add(state);
                }
            }

            if (available.isEmpty()) return s1;
            else s1 = available.get(r.nextInt(available.size()));
        }
        // If p=O and not all states are output-complete
        else if(settings.isOutputComplete() && !PropP)
        {
            List<State_> available = new ArrayList<>();

            // Finds suitable state
            for (State_ state : iolts.getReachableStates()) {
                if(!state.getOut().containsAll(iolts.getOutputs()))
                {
                    available.add(state);
                }
            }

            if (available.isEmpty()) return s1;
            else s1 = available.get(r.nextInt(available.size()));
        }
        // Else (p=I or p=O but PropP = true, or p is omitted)
        else{
            List<State_> available = new ArrayList<>();

            if(settings.isDeterministic())
            {
                // Finds state that isn't completely specified
                for (State_ state : iolts.getReachableStates()) {
                    if(!state.getIn().containsAll(iolts.getInputs()) || !state.getOut().containsAll(iolts.getOutputs()))
                    {
                        available.add(state);
                    }
                }

            }
            else
            {
                // Tries to find state that isn't completely specified
                for (State_ state : iolts.getReachableStates()) {
                    if(!state.getIn().containsAll(iolts.getInputs()) || !state.getOut().containsAll(iolts.getOutputs()))
                    {
                        available.add(state);
                    }
                }

                // Will repeat outputs, but not inputs
                if(settings.isInputDeterminism())
                {
                    // If all are completely specified, tries any
                    if (available.isEmpty()){
                        // Finds state s1 that doesn't have a transition (s1, l, s2)
                        // for at least one pair (l, s2) where l is an output
                        for (State_ origin : iolts.getReachableStates()) {
                            for (State_ target : iolts.getStates()) {
                                for (String label : iolts.getOutputs()) {
                                    Transition_ t = new Transition_(origin, label, target);
                                    if(!iolts.getTransitions().contains(t) && !available.contains(origin)) {
                                        available.add(origin);
                                    }
                                }
                            }
                        }
                    }
                }
                // Will repeat inputs, but not outputs
                else if(settings.isOutputDeterminism())
                {
                    // If all are completely specified, tries any
                    if (available.isEmpty()){
                        // Finds state s1 that doesn't have a transition (s1, l, s2)
                        // for at least one pair (l, s2) where l is an input
                        for (State_ origin : iolts.getReachableStates()) {
                            for (State_ target : iolts.getStates()) {
                                for (String label : iolts.getInputs()) {
                                    Transition_ t = new Transition_(origin, label, target);
                                    if(!iolts.getTransitions().contains(t) && !available.contains(origin)) {
                                        available.add(origin);
                                    }
                                }
                            }
                        }
                    }
                }
                // Will repeat both inputs and outputs
                else {
                    // If all are completely specified, tries any
                    if (available.isEmpty()){
                        // Finds state s1 that doesn't have a transition (s1, l, s2)
                        // for at least one pair (l, s2)
                        for (State_ origin : iolts.getReachableStates()) {
                            for (State_ target : iolts.getStates()) {
                                for (String label : iolts.getAlphabet()) {
                                    Transition_ t = new Transition_(origin, label, target);
                                    if(!iolts.getTransitions().contains(t) && !available.contains(origin)) {
                                        available.add(origin);
                                    }
                                }
                            }
                        }
                    }
                }

            }

            // Random among available results
            if (available.isEmpty()) return s1;
            else s1 = available.get(r.nextInt(available.size()));
        }

        return s1;
    }

    /**
     * Method for selecting a target state for a new transition
     * @param iolts Incomplete IOLTS model
     * @return selected state
     */
    public static State_ setState2(IOLTS iolts, State_ s1, boolean a) {
        State_ s2 = null;
        Random r = new Random();

        // If S \ RS != empty
        if(!iolts.getReachableStates().containsAll(iolts.getStates()))
        {
            List<State_> available = new ArrayList<>();

            // Lists suitable states
            for (State_ state : iolts.getStates()) {
                if(!iolts.getReachableStates().contains(state))
                {
                    available.add(state);
                }
            }

            s2 = available.get(r.nextInt(available.size()));
            iolts.addReachableState(s2);
        }
        else if(a)
        {
            List<State_> checked = new ArrayList<>();
            checked.add(s1);
            boolean R = true;
            while (R && !checked.containsAll(iolts.getStates()))
            {
                do {
                    // Gets random state
                    int i = r.nextInt(iolts.getStates().size());
                    s2 = iolts.getStates().get(i);
                }while(s2.getName() == s1.getName() || checked.contains(s2));
                // State s2 can't be s1, can't have been checked

                iolts.markAllNotVisited(); // Resets visited status of all states
                R = checkDescendant(s1, s2);
                checked.add(s2);
            }

            if(R) {
                return null; // Returns null if R remained true (did not find available state)
            }
        }
        else
        {
            if(settings.isDeterministic())
            {
                s2 = iolts.getReachableStates().get(r.nextInt(iolts.getReachableStates().size()));
            }
            else
            {
                List<State_> available = new ArrayList<>();
                List<String> availabels = new ArrayList<>();

                // States without transitions from s1
                boolean found = false;
                for(State_ state : iolts.getReachableStates())
                {
                    for(Transition_ t : s1.getTransitions())
                    {
                        if(t.getEndState().equals(state)) found = true;
                    }

                    if(!found) available.add(state);
                }

                if(settings.isInputDeterminism())
                {
                    // States with transitions from s1
                    for(State_ state : iolts.getReachableStates())
                    {
                        // For each state, check if there is a corresponding transition
                        // from state s1
                        availabels = new ArrayList<>();
                        availabels.addAll(iolts.getAlphabet());
                        for(Transition_ t : s1.getTransitions())
                        {
                            if(iolts.getInputs().contains(t.getLabel()))
                            {
                                // If there is, remove label from available list
                                availabels.remove(t.getLabel());
                            }

                            if(t.getEndState().equals(state))
                            {
                                // If there is, remove label from available list
                                availabels.remove(t.getLabel());
                            }
                        }

                        // If there are labels are available,
                        // add state to available s2
                        if(!availabels.isEmpty() && !available.contains(state)){
                            available.add(state);
                        }
                    }
                }
                else if(settings.isOutputDeterminism())
                {
                    // States with transitions from s1
                    for(State_ state : iolts.getReachableStates())
                    {
                        // For each state, check if there is a corresponding transition
                        // from state s1
                        availabels = new ArrayList<>();
                        availabels.addAll(iolts.getAlphabet());
                        for(Transition_ t : s1.getTransitions())
                        {
                            if(iolts.getOutputs().contains(t.getLabel()))
                            {
                                // If there is, remove label from available list
                                availabels.remove(t.getLabel());
                            }

                            if(t.getEndState().equals(state))
                            {
                                // If there is, remove label from available list
                                availabels.remove(t.getLabel());
                            }
                        }

                        // If there are labels are available,
                        // add state to available s2
                        if(!availabels.isEmpty() && !available.contains(state)){
                            available.add(state);
                        }
                    }
                }
                else {
                    // States with transitions from s1
                    for(State_ state : iolts.getReachableStates())
                    {
                        // For each state, check if there is a corresponding transition
                        // from state s1
                        availabels = new ArrayList<>();
                        availabels.addAll(iolts.getAlphabet());
                        for(Transition_ t : s1.getTransitions())
                        {
                            if(t.getEndState().equals(state))
                            {
                                // If there is, remove label from available list
                                availabels.remove(t.getLabel());
                            }
                        }

                        // If there are labels are available,
                        // add state to available s2
                        if(!availabels.isEmpty() && !available.contains(state)){
                            available.add(state);
                        }
                    }
                }

                // Random among available results
                if(!available.isEmpty()){
                    s2 = available.get(r.nextInt(available.size()));
                }
            }
        }

        return s2;
    }

    /**
     * Method for generating one basic transition
     * @param s1 Source state
     * @param s2 Target state
     * @param alphabet Alphabet from which to select a label for the transition
     * @param iolts Incomplete IOLTS model
     * @return generated transition
     */
    public static Transition_ createTransition(State_ s1, State_ s2, List<String> alphabet, IOLTS iolts)
    {
        List<String> available = new ArrayList<>();

        if(settings.isDeterministic())
        {
            // Generates list of labels that have no corresponding transition from s1
            for (String label : alphabet) {
                if(!s1.getIn().contains(label) && !s1.getOut().contains(label))
                {
                    available.add(label);
                }
            }
        }
        else {

            if(settings.isInputDeterminism())
            {
                // Removes labels that would result in duplicated transition
                // and inputs that have already been used for any state s2
                available.addAll(iolts.getAlphabet());
                for (Transition_ t : s1.getTransitions())
                {
                    if(iolts.getInputs().contains(t.getLabel()))
                    {
                        available.remove(t.getLabel());
                    }

                    if(t.getEndState().equals(s2))
                    {
                        available.remove(t.getLabel());
                    }
                }
            }
            else if(settings.isOutputDeterminism())
            {
                // Removes labels that would result in duplicated transition
                // and outputs that have already been used for any state s2
                available.addAll(iolts.getAlphabet());
                for (Transition_ t : s1.getTransitions())
                {
                    if(iolts.getOutputs().contains(t.getLabel()))
                    {
                        available.remove(t.getLabel());
                    }

                    if(t.getEndState().equals(s2))
                    {
                        available.remove(t.getLabel());
                    }
                }
            }
            else{
                // Generates list of labels that have no corresponding transition from s1 to s2
                // to avoid repeating transitions
                available.addAll(iolts.getAlphabet());
                for (Transition_ t : s1.getTransitions())
                {
                    if(t.getEndState().equals(s2))
                    {
                        available.remove(t.getLabel());
                    }
                }
            }

        }

        // Chooses randomly from available labels
        Random r = new Random();
        String label = available.get(r.nextInt(available.size()));

        // Creates transitions
        Transition_ t = new Transition_(s1, label, s2);
        iolts.addTransition(t);
        s1.addTransition(t);
        unusedLabels.remove(label);

        // Updates adjacency of s1
        s1.addAdj(s2);

        if(iolts.getInputs().contains(label)) s1.addIn(label);
        else if(iolts.getOutputs().contains(label)) s1.addOut(label);

        return t;
    }

    /**
     * Method for generating transitions with specific deterministic properties
     * such as input-determinism or output-determinism.
     *
     * @param s1 Source state
     * @param s2 Target state
     * @param iolts Incomplete IOLTS model
     * @return list of transitions with generated transitions included
     */
    public static List<Transition_> createDeterministicTransition(State_ s1, State_ s2, IOLTS iolts)
    {

        if(settings.isOutputDeterminism() && s1.getOut().isEmpty())
        {
            Transition_ t = createTransition(s1, s2, iolts.getOutputs(), iolts);
        }

        if(settings.isInputDeterminism() && s1.getIn().isEmpty())
        {
            Transition_ t = createTransition(s1, s2, iolts.getInputs(), iolts);
        }

        return iolts.getTransitions();
    }

    /**
     * Method for generating transitions with specific deterministic and completeness
     * properties such as input-completeness, output-determinism, completely specified,
     * and possible combinations.
     *
     * @param s1 Source state
     * @param s2 Target state
     * @param iolts Incomplete IOLTS model
     * @return list of transitions with generated transitions included
     */
    public static List<Transition_> createCompleteTransition(State_ s1, State_ s2, IOLTS iolts)
    {
        if(settings.isInputComplete() && !s1.getIn().containsAll(iolts.getInputs()))
        {
            Transition_ t;

            if(settings.isOutputDeterminism() && s1.getOut().isEmpty())
            {
                t = createTransition(s1, s2, iolts.getOutputs(), iolts);
            }
            else
            {
                t = createTransition(s1, s2, iolts.getInputs(), iolts);
            }

        }
        if(settings.isOutputComplete() && !s1.getOut().containsAll(iolts.getOutputs()))
        {
            Transition_ t;

            if(settings.isInputDeterminism() && s1.getIn().isEmpty())
            {
                t = createTransition(s1, s2, iolts.getInputs(), iolts);
            }
            else
            {
                t = createTransition(s1, s2, iolts.getOutputs(), iolts);
            }

        }

        return iolts.getTransitions();
    }

    /**
     * Checks the deterministic property for specific sets according to the settings
     * @param iolts Incomplete IOLTS model
     * @return true or false for set-specific determinism
     */
    public static boolean checkDeterminism(IOLTS iolts)
    {
        if(settings.isInputDeterminism() && settings.isOutputDeterminism())
        {
            for (State_ state : iolts.getStates()) {
                if(state.getIn().isEmpty()) return false;
            }

            for (State_ state : iolts.getStates()) {
                if(state.getIn().isEmpty()) return false;
            }
        }
        else if(settings.isInputDeterminism())
        {
            for (State_ state : iolts.getStates()) {
                if(state.getIn().isEmpty()) return false;
            }
        }
        else if(settings.isOutputDeterminism())
        {
            for (State_ state : iolts.getStates()) {
                if(state.getIn().isEmpty()) return false;
            }
        }

        return true;
    }

    /**
     * Checks the completeness property for specific sets according to the settings
     * @param iolts Incomplete IOLTS model
     * @return true or false for set-specific completeness
     */
    public static boolean checkCompleteness(IOLTS iolts)
    {
        if(settings.isInputComplete() && settings.isOutputComplete())
        {
            // p = I and p = O, if any state doesn't have transitions defined for all inputs and outputs
            // returns false
            for (State_ state : iolts.getStates()) {
                if( !state.getIn().containsAll(iolts.getInputs()) || !state.getOut().containsAll(iolts.getOutputs()))
                {
                    return false;
                }
            }
        }
        else if(settings.isInputComplete())
        {
            // p = I, if any state doesn't have transitions defined for all inputs
            // returns false
            for (State_ state : iolts.getStates()) {
                if(!state.getIn().containsAll(iolts.getInputs()))
                {
                    return false;
                }
            }
        }
        else if(settings.isOutputComplete())
        {
            // p = O, if any state doesn't have transitions defined for all outputs
            // returns false
            for (State_ state : iolts.getStates()) {
                if(!state.getOut().containsAll(iolts.getOutputs())) return false;
            }
        }

        return true;
    }

    /** Checks if state s1 can be reached (is a descendant) of state s2.
     * @param s1 state to be tested as descendant
     * @param s2 state to be tested as parent
     * @return true or false
     */
    public static boolean checkDescendant(State_ s1, State_ s2)
    {
        if(s2.getAdj().size() == 0)
        {
            return false;
        }
        else if(s2.getAdj().contains(s1))
        {
            return true;
        }
        else
        {
            boolean R = false;

            for (State_ s : s2.getAdj()) {
                if(!s.isVisited() && R == false)
                {
                    s.setVisited(true);
                    R = checkDescendant(s1, s);
                }
            }
            return R;
        }
    }

    /* GETTERS AND SETTERS */

    public static Settings getSettings() {
        return settings;
    }

    public static void setSettings(Settings settings) {
        GenerateIOLTS.settings = settings;
    }
}
