package cz.cyberrange.platform.training.feedback.constants;

import org.springframework.stereotype.Component;

@Component
public class GraphConstants {

    public static final String GREEN = "#2ca02c";
    public static final String RED = "#E45045";
    public static final String YELLOW = "#FFDE2E";
    public static final String BLACK = "#000000";
    public static final String GRAY = "#D7D9CE";
    public static final String LIGHT_BLUE = "#81d4fa";
    public static final String VIOLET = "#E0BBE4";
    public static final String LIGHTGREY = "lightgrey";

    public static final String DIAMOND = "diamond";


    public static final String NODE_STYLE = "filled";
    public static final String GRAPH_TYPE = "digraph";
    public static final String GRAPH_FOOTER = "}\n";

    public static final String REFERENCE_GRAPH_LABEL = "Reference graph";
    public static final String TRAINEE_GRAPH_LABEL = "Trainee graph #";

    public static final long FIRST_LEVEL_ID = 1L;

    public static final String START_NODE_LABEL = "start";
    public static final String NOT_IN_REFERENCE = "Not in reference graph\n After: ";
    public static final String TRIED_TO_REACH = "Tried to reach: ";
    public static final String MISSING_NODES = " Missing nodes: ";

    private GraphConstants() {
    }
}
