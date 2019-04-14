package server;

public class BakeryAlgorithm {

    private static boolean d;//switch boolean 
    private static boolean[] c;//array holding the position for each token 
    private static int[] tk; //token array
    private static int n;//number of processes

    public BakeryAlgorithm(int num_variables) {
        n = num_variables;
        c = new boolean[n];
        tk = new int[n];
        d = false;
    }

    public void bakery(int process_id) {         //token selection and wait algorithm. Sent the id of the process waiting
        c[process_id] = true;                    //tells the other processes that a token is being made in the position
        if (d == false) {               //determines direction of the token, in this case positive
            tk[process_id] = (findMax() + 1);    //increases the max token value and places it in the array
            c[process_id] = false;               //tells the other processes that the allocation is finished
            for (int j = 0; j < n; j++) {//goes through the array
                while (c[j] == true) {
                    System.out.print("");
                }                       //waits on any processes entering the queue
                while ((tk[j] > 0 && (tk[j] < tk[process_id] || (tk[j] == tk[process_id] && j < process_id))) || (tk[j] < 0 && d == false)) {
                    System.out.print("");
                }
            }
            //waits for the earliest process in the queue or if the value is negative and the switch boolean is false, then the token gets to go
            if (d == false) {//switches the boolean to true so negative list can be created
                d = true;
            }
        } else {//see above but in the negative positions
            tk[process_id] = (findMin() - 1);
            c[process_id] = false;
            for (int j = 0; j < n; j++) {
                while (c[j] == true) {
                    System.out.print("");
                }
                while ((tk[j] < 0 && (tk[j] > tk[process_id] || (tk[j] == tk[process_id] && j > process_id))) || (tk[j] > 0 && d == true)) {
                    System.out.print("");
                }
            }
            if (d == true) {
                d = false;
            }
        }
    }

    public void bakeryExit(int i) {//releases the token 
        tk[i] = 0;
    }

    private int findMax() {//determines the largest token in the array 
        int temp = 0;
        for (int i = 0; i < tk.length; i++) {
            if (tk[i] >= temp) {
                temp = tk[i];
            }
        }
        return temp;
    }

    private int findMin() {//determines the smallest token in the array
        int temp = 0;
        for (int i = 0; i < tk.length; i++) {
            if (tk[i] <= temp) {
                temp = tk[i];
            }
        }
        return temp;
    }
}
