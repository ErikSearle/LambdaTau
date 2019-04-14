package UsefulTools;

public class ColourErrors {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public void redError(String error){
        System.out.println(ANSI_RED + error + ANSI_RESET);
    }
    public void blackError(String error){
        System.out.println(ANSI_BLACK + error + ANSI_RESET);
    }
    public void greenError(String error){
        System.out.println(ANSI_GREEN + error + ANSI_RESET);
    }
    public void yellowError(String error){
        System.out.println(ANSI_YELLOW + error + ANSI_RESET);
    }
    public void blueError(String error){
        System.out.println(ANSI_BLUE + error + ANSI_RESET);
    }
    public void purpleError(String error){
        System.out.println(ANSI_PURPLE + error + ANSI_RESET);
    }
    public void cyanError(String error){
        System.out.println(ANSI_CYAN + error + ANSI_RESET);
    }
}
