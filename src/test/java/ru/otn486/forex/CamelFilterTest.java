package ru.otn486.forex;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;
import ru.otn486.forex.filter.CurrencyFilter;
import java.io.File;
import java.net.URL;


public class CamelFilterTest extends CamelTestSupport {

    File mockData;

    @Before
    public void before(){
        URL url = this.getClass().getResource("/testData.xml");
        mockData = new File(url.getFile());
    }

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Test
    public void splitMessage() throws InterruptedException{
        resultEndpoint.setExpectedMessageCount(15);
        template.sendBody("direct:split", mockData);
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void filterByName() throws InterruptedException{
        resultEndpoint.setExpectedMessageCount(11);
        template.sendBody("direct:filterByName", mockData);
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void filterBySpread() throws InterruptedException{
        resultEndpoint.setExpectedMessageCount(12);
        template.sendBody("direct:filterBySpread", mockData);
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void filterNullParameter() throws InterruptedException{
        try {
            template.sendBody("direct:filterNull",
                    "<currency name='USDRUR'>" +
                              "<buyPrice>66.5555</buyPrice>" +
                              "<sellPrice>66.8888</sellPrice>" +
                          "</currency>");
        } catch (CamelExecutionException e){
            Assert.isInstanceOf(java.lang.NullPointerException.class, e.getCause());
        }
    }

    @Test
    public void filterDoubleInvalid() throws InterruptedException{
        template.sendBody("direct:filterBySpread",
                    "<currency name='USDRUR'>" +
                            "<buyPrice>invalid</buyPrice>" +
                            "<sellPrice>66.8888</sellPrice>" +
                            "</currency>");
    }

    @Override
    protected RoutesBuilder[] createRouteBuilders() throws Exception {

        RouteBuilder routeSplit = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:split")
                        .split(xpath("//currency"))
                        .to("mock:result")
                        .log("${body}");
            }
        };

        RouteBuilder routeFilterByName = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:filterByName")
                        .split(xpath("//currency"))
                        .filter().method(new CurrencyFilter("USD"), "isBaseCurrency")
                        .to("mock:result")
                        .log("${body}");
            }
        };
        RouteBuilder routeFilterBySpread = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:filterBySpread")
                        .split(xpath("//currency"))
                        .filter().method(new CurrencyFilter(0.01), "isSpreadLess")
                        .to("mock:result")
                        .log("${body}");
            }
        };
        RouteBuilder routeNullFilter = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:filterNull")
                        .split(xpath("//currency"))
                        .filter().method(new CurrencyFilter("USD"), "isSpreadLess")
                        .to("mock:result")
                        .log("${body}");
            }
        };
        return new RoutesBuilder[] {routeSplit, routeFilterByName, routeFilterBySpread, routeNullFilter};
    }

}
