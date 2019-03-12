package ru.otn486.forex.filter;

import org.apache.camel.language.XPath;
import org.slf4j.Logger;
import java.util.Objects;
import static org.slf4j.LoggerFactory.getLogger;

public class CurrencyFilter {

    private static final Logger log = getLogger(CurrencyFilter.class);

    String currencyName;
    Double spread;

    public CurrencyFilter(double spread) {
        this.spread = spread;
    }

    public CurrencyFilter(String currencyName) {
        this.currencyName = currencyName;
    }

    public CurrencyFilter(String currencyName, double spread) {
        this.currencyName = currencyName;
        this.spread = spread;
    }

    //Фильтр по базовой валюте(первая вылюта в валютной паре)
    public boolean isBaseCurrency(@XPath("/currency/@name") String name){
        Objects.requireNonNull(currencyName,
                "You have added filter in route without a comparable value");
        return currencyName.equals(name.substring(0,3));
    }

    //Фильтр по котируемой валюте(вторая вылюта в валютной паре)
    public boolean isCounterCurrency(@XPath("/currency/@name") String name){
        Objects.requireNonNull(currencyName,
                "You have added filter in route without a comparable value");
        return currencyName.equals(name.substring(3));
    }

    //Фильтр по спрэду(разница между ценами покупки и продажи) меньшему заданного значения
    public boolean isSpreadLess(@XPath("/currency/buyPrice/text()") String buyPrice,
                                @XPath("/currency/sellPrice/text()") String sellPrice){
        double buy;
        double sell;
        try {
             buy = Double.valueOf(buyPrice);
             sell = Double.valueOf(sellPrice);
        } catch (NumberFormatException e) {
             log.warn("Input data is not valid. Filter was skipped");
             return false;
        }

        Objects.requireNonNull(spread, "You have added filter in route without a comparable value");
        return spread > 0 ? (sell-buy) < spread : false;
    }
}
