import java.util.*;

class Process{
    int id;
    int arrivalTime;
    int burstTime;
    int priority;
    int remainingTime;
    int waitingTime;
    int turnaroundTime;
    int responseTime;

    public Process(int id, int arrivalTime, int burstTime, int priority){
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.responseTime = -1;
    }
}

public class MultilevelFeedbackQueueSchedulingAlgorithm{
    public static void main(String[] args){
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter the number of Queues: ");
            int numQueues = sc.nextInt();
            int[] quantumTimes = new int[numQueues];
            for(int i = 0; i < numQueues; i++){
                System.out.print("Enter the Quantum Time for Queue " + (i + 1) + ": ");
                quantumTimes[i] = sc.nextInt();
            }
            System.out.print("Enter the Aging Time: ");
            int agingTime = sc.nextInt();
            System.out.print("Enter the number of Processes: ");
            int numProcesses = sc.nextInt();
            Process[] processes = new Process[numProcesses];
            for(int i = 0; i < numProcesses; i++){
                System.out.print("Enter the Process id: ");
                int id = sc.nextInt();
                System.out.print("Enter the Arrival Time for Process " + id + ": ");
                int arrivalTime = sc.nextInt();
                System.out.print("Enter the Burst Time for Process " + id + ": ");
                int burstTime = sc.nextInt();
                System.out.print("Enter the Priority for Process " + id + ": ");
                int priority = sc.nextInt();
                processes[i] = new Process(id, arrivalTime, burstTime, priority);
            }
            Queue<Process>[] queues = new LinkedList[numQueues];
            for(int i = 0; i < numQueues; i++){
                queues[i] = new LinkedList<>();
            }
            PriorityQueue<Process> agingQueue = new PriorityQueue<>(new Comparator<Process>(){
                @Override
                public int compare(Process p1, Process p2){
                    return p1.priority - p2.priority;
                }
            });
            List<Process> completedProcesses = new ArrayList<>();
            for(Process process : processes){
                queues[0].add(process);
            }
            int currentTime = 0;
            while (!queuesEmpty(queues) || !agingQueue.isEmpty()){
                boolean executedProcess = false;
                while (!agingQueue.isEmpty() && currentTime - agingQueue.peek().waitingTime >= agingTime){
                    Process processToMoveUp = agingQueue.poll();
                    processToMoveUp.priority--;
                    queues[processToMoveUp.priority].add(processToMoveUp);
                }
                for(int i = 0; i < numQueues; i++){
                    if(!queues[i].isEmpty()){
                        Process currentProcess = queues[i].poll();
                        executedProcess = true;
                        if(currentProcess.responseTime == -1){
                            currentProcess.responseTime = currentTime - currentProcess.arrivalTime;
                        }
                        if(currentProcess.remainingTime > quantumTimes[i]){
                            currentProcess.remainingTime -= quantumTimes[i];
                            currentTime += quantumTimes[i];
                            currentProcess.waitingTime = currentTime;
                            if(i < numQueues - 1){
                                currentProcess.priority++;
                                queues[currentProcess.priority].add(currentProcess);
                            }else{
                                agingQueue.add(currentProcess);
                            }
                        }else{
                            currentTime += currentProcess.remainingTime;
                            currentProcess.remainingTime = 0;

                            currentProcess.turnaroundTime = currentTime - currentProcess.arrivalTime;
                            completedProcesses.add(currentProcess);
                        }
                        break;
                    }
                }
                if(!executedProcess){
                    currentTime++;
                }
            }
            for(Process process : completedProcesses){
                process.waitingTime = process.turnaroundTime - process.burstTime;
            }
            System.out.println("");
            System.out.println("Execution Sequence (Gantt chart): ");
            for(Process process : completedProcesses){
                System.out.print("|  P" + process.id + "  ");
            }
            System.out.println("|");
            int time = 0;
            System.out.print("0");
            for(Process process : completedProcesses){
                time += process.burstTime;
                String spaces = new String(new char[6]).replace('\0', ' ');
                System.out.format("%s%d", spaces, time);
            }
            System.out.println("");
            System.out.print("------------------------------------------------------------------------------------------------");
            System.out.println("\nProcesses\tArrival Times\tBurst Times\tResponse Times\tWaiting Times\tTurnaround Times");
            for(Process process : processes){
                System.out.println("P" + process.id + "\t\t" + process.arrivalTime + "\t\t" + process.burstTime + "\t\t"
                        + process.responseTime + "\t\t" + process.waitingTime + "\t\t" + process.turnaroundTime);
            }
            System.out.println("------------------------------------------------------------------------------------------------");
            double totalTurnaroundTime = 0;
            double totalWaitingTime = 0;
            for(Process process : processes){
                totalTurnaroundTime += process.turnaroundTime;
                totalWaitingTime += process.waitingTime;
            }
            double averageTurnaroundTime = totalTurnaroundTime / numProcesses;
            double averageWaitingTime = totalWaitingTime / numProcesses;
            System.out.printf("Average Turnaround Time: %.2f\n", averageTurnaroundTime);
            System.out.printf("Average Waiting Time: %.2f\n", averageWaitingTime);
        }
    }

    public static boolean queuesEmpty(Queue<Process>[] queues){
        for(Queue<Process> queue : queues){
            if(!queue.isEmpty()){
                return false;
            }
        }
        return true;
    }
}