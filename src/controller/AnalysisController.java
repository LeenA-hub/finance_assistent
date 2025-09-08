package controller;

public class AnalysisController {
    @FXML
    private LineChart<String, Number> trendChart;
    @FXML
    private VBox analysisCards;

    public void initialize() {
        loadTrendData();
    }

    private void loadTrendData() {
        // Example: add cards
        Label savingCard = new Label("Total Savings: $XXX");
        savingCard.getStyleClass().add("card");
        analysisCards.getChildren().add(savingCard);

        Label highestSpendCard = new Label("Highest Spend: Food - $XXX");
        highestSpendCard.getStyleClass().add("card");
        analysisCards.getChildren().add(highestSpendCard);
    }

}
