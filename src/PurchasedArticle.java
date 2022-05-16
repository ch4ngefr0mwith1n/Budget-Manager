package budget;

import java.io.Serializable;
import java.util.Objects;

public class PurchasedArticle implements Serializable {
    private String articleName;
    private double price;
    private Category category;

    public PurchasedArticle(String articleName, double price, Category category) {
        this.articleName = articleName;
        this.price = price;
        this.category = category;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchasedArticle that = (PurchasedArticle) o;
        return Double.compare(that.price, price) == 0 && Objects.equals(articleName, that.articleName) && category == that.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleName, price, category);
    }

    @Override
    public String toString() {
        return "PurchasedArticle{" +
                "articleName='" + articleName + '\'' +
                ", price=" + price +
                ", category=" + category.getName() +
                '}';
    }


}