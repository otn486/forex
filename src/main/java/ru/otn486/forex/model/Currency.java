package ru.otn486.forex.model;

import javax.xml.bind.annotation.*;

@XmlType(name = "currency")
@XmlAccessorType(XmlAccessType.FIELD)
public class Currency {

    @XmlAttribute(name = "name")
    String name;

    @XmlElement(name = "buyPrice")
    double buyPrice;

    @XmlElement(name = "sellPrice")
    double sellPrice;

    public Currency() {
    }

    public Currency(String name, double buyPrice, double sellPrice) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "name='" + name + '\'' +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                '}';
    }
}
