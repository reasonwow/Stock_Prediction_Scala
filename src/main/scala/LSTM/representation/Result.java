package LSTM.representation;

public class Result {
    private double[] predicts;
    private double[] actuals;

    public Result(double[] predicts, double[] actuals) {
        this.predicts = predicts;
        this.actuals = actuals;
    }

    public double[] getPredicts() {
        return predicts;
    }

    public void setPredicts(double[] predicts) {
        this.predicts = predicts;
    }

    public double[] getActuals() {
        return actuals;
    }

    public void setActuals(double[] actuals) {
        this.actuals = actuals;
    }
}
