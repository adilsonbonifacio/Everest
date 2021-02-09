package parser;

import model.IOLTS;
import model.State_;
import model.Transition_;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExportAutFile {
    public ExportAutFile() { }

    public static List<String> IOTLStoAut(String setName, IOLTS iolts)
    {
        List<String> aut = new ArrayList<String>();

        // Header
        String stringHeader = new String("des ("
                + iolts.getInitialState().getName()
                + ","
                + iolts.getTransitions().size()
                + ","
                + iolts.getStates().size()
                + ")");

        aut.add(stringHeader);

        // Transitions
        for(State_ state : iolts.getStates())
        {
            for(Transition_ transition : state.getTransitions())
            {
                // Verify if label is input or output
                String label;

                if(iolts.getInputs().contains(transition.getLabel()))
                {
                    label = new String("?" + transition.getLabel());
                }
                else if(iolts.getOutputs().contains(transition.getLabel()))
                {
                    label = new String("!" + transition.getLabel());
                }
                else { label = transition.getLabel(); }


                // Write transition
                String stringTransition = new String("("
                        + transition.getIniState().getName()
                        + ","
                        + label
                        + ","
                        + transition.getEndState().getName()
                        + ")"
                );

                aut.add(stringTransition);
            }
        }

        return aut;
    }

    public static void writeIOLTS(String setName, String path, ArrayList<IOLTS> iolts)
    {
        int count = 1;
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        File dir = new File(path + File.separator + setName+ "_" + timeStamp);

        // Checks if dir exists
        if (Files.exists(dir.toPath()))
        {
            int countdir = 0;

            while(Files.exists(dir.toPath()))
            {
                countdir++;
                dir = new File(path + File.separator + setName + countdir + "_" + timeStamp);
            }

        }

        dir.mkdir();

        for (IOLTS io: iolts) {
            // Creates file
            Path file = Paths.get(dir.getAbsolutePath().toString(), new String(setName+count+".aut"));

            // Translates IOLTS to .aut formatting
            List<String> aut = ExportAutFile.IOTLStoAut(setName, io);

            // Attempts to write to file
            count++;
            try{
                Files.write(file, aut, StandardCharsets.UTF_8);
            }
            catch(IOException e) { e.printStackTrace(); }
        }
    }
}
