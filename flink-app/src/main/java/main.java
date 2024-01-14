import flink.jobs.SinglePinotJob;

public class main {
    public static void main(String[] args) throws Exception {
        SinglePinotJob pinotJob = new SinglePinotJob();
        pinotJob.run();
    }
}
