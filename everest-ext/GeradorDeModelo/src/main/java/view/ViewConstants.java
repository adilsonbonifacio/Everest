package view;

public class ViewConstants {
    public static final String TAU = "tau";

    public static final String typeAutomaticLabel = "?in, !out";
    public static final String typeManualLabel = "define I/O manually";
    public static final String LTS_CONST = "LTS";
    public static final String IOLTS_CONST = "IOLTS";
    public static final String tabIOCO = "IOCO Conformance";
    public static final String tabLang = "Language Based Conformance";
    public static final String tabTSGeneration = "Test Generation & Run";
    public static final String tabTestRun = "Run in batch";
    public static final String toolName = "Everest";
    public static final String[] models = new String[] { "", "IOLTS", "LTS" };
    public static final String folderIconPath = "/img/folder.png";
    public static final String titleFrameImgImplementation = "Implementation - ";
    public static final String titleFrameImgSpecification = "Model - ";



    // message
	/*public static final String modelWithoutTransition = "Model without transition, if you selected the option "
			+ typeAutomaticLabel + " the transitions must contain such tags(!/?) \n";
	public static final String implementationWithoutTransition = "Model without transition, if you selected the option "
			+ typeAutomaticLabel + " the transitions must contain such tags(!/?) \n";*/

    public static final String msgImp = "Decoration ? and ! are missing in the input files \n";//"The implementation transitions are not labeled with '?' and '!'\n ";
    public static final String msgModel = "Decoration ? and ! are missing in the input files \n";//"The model transitions are not labeled with '?' and '!'\n ";
    public static final String exceptionMessage = "An unexpected error ocurred \n";
    public static final String invalidRegex = " Invalid regex! \n";
    public static final String selectModel = "Select the model type \n";//"Select the kind of model \n";
    public static final String selectImplementation = "Select the IUT model \n";//"The field Implementation is required \n";
    public static final String selectSpecification = "Select the specification model \n";//"The field Model is required \n";
    public static final String selectIoltsLabel = "Choose the I/O partition mode \n";//"It is necessary how the IOLTS labels will be distinguished \n";
    public static final String selectInpOut = "Define the input and output labels \n";//"The fields Input and Output is required \n";
    public static final String selectIolts = "IOCO relation is defined over IOLTS models \n";//"The informed model must be IOLTS \n";
    public static final String selectIolts_gen = "Test generation is defined over IOLTS models \n";//"The informed model must be IOLTS \n";
    public static final String labelInpOut="There labels that were not set as input/output \n";

    public static final String mInteger="Max IUT states must be an integer number \n";
    public static final String ntcInteger="# Test purposes must be an integer number \n";
    public static final String generation_mult = "* Provide the 'Specification' and '# Max iut states' to generate the multigraph \n";
    public static final String generation = "* Provide the 'Specification', '# Max iut states' and '# Teste purposes'  to generate the multigraph and the respective TPs \n";
    public static final String run_generation = "* Provide the 'Specification', 'Implementation', '# Max iut states' and '# Teste purposes’ to generate the multigraph, and to generate and run the respective TPs \n";
    public static final String generation_tp_from_multi = "* Provide the 'Multigraph' and '# Teste purposes' to extract the respective TPs \n";
    public static final String multigraph_generation = "* Provide the 'Implementation', 'Multigraph' and '# Teste purposes' to extract and run the respective TPs \n";
    public static final String run_tp= "* Provide the 'Implementation' and 'Test purpose folder'  to run the selected TPs \n";
    public static final String selectSpecification_iut = "Select the specification or/and implementation model \n";
    public static final String genRun_noFault = "no fault was found according to the test run.";
    public static final String genRun_fault =   "a fault was found.";

    public static final String btnrunTpTip = "Run TPs over the selected IUT";
    public static final String btnRunMultigraphTip = "Extract TPs from the selected multigraph and run over the selected IUT";
    public static final String btnRunGenerateTip1 = "Generate the multigraph of S, the corresponding TPs and run them over the selected IUT";
    public static final String btnRunGenerateTip2 = "Extract TPs from the selected multigraph, and run them  over the selected IUT";
    public static final String btnGenerateTip1 = "Generate the multigraph of S ";
    public static final String btnGenerateTip2 = "Extract TPs from the selected multigraph ";


    public static final String selectTpRunMode = "Select test purpose run mode \n";
    public static final String selectOneTp= "Select the test purpose \n";
    public static final String selectTpFolder= "Select test purposes folder \n";

    public static final String selectIutRunMode = "Select impletation run mode \n";
    public static final String selectOneIut= "Select the implementation \n";
    public static final String selectIutFolder= "Select implentation folder\n";

    public static final String selectPathSaveVerdict= "Select the path to save the verdict \n";

}
