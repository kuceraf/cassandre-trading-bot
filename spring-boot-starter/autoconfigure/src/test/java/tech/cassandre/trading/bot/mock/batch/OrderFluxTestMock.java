package tech.cassandre.trading.bot.mock.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@TestConfiguration
public class OrderFluxTestMock extends BaseTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Bean
    @Primary
    public TickerFlux tickerFlux() {
        return new TickerFlux(marketService());
    }

    @Bean
    @Primary
    public AccountFlux accountFlux() {
        return new AccountFlux(userService());
    }

    @Bean
    @Primary
    public OrderFlux orderFlux() {
        return new OrderFlux(tradeService(), orderRepository);
    }

    @SuppressWarnings("unchecked")
    @Bean
    @Primary
    public UserService userService() {
        Map<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();
        final Map<String, AccountDTO> accounts = new LinkedHashMap<>();
        UserService userService = mock(UserService.class);
        // Returns three updates.

        // Account 01.
        BalanceDTO account01Balance1 = BalanceDTO.builder().available(new BigDecimal("1")).build();
        balances.put(BTC, account01Balance1);
        AccountDTO account01 = AccountDTO.builder().accountId("01").name("trade").balances(balances).build();
        accounts.put("01", account01);
        UserDTO user01 = UserDTO.builder().accounts(accounts).build();
        balances.clear();
        accounts.clear();

        // Account 02.
        BalanceDTO account02Balance1 = BalanceDTO.builder().available(new BigDecimal("1")).build();
        balances.put(BTC, account02Balance1);
        AccountDTO account02 = AccountDTO.builder().accountId("02").name("trade").balances(balances).build();
        accounts.put("02", account02);
        UserDTO user02 = UserDTO.builder().accounts(accounts).build();
        balances.clear();
        accounts.clear();

        // Account 03.
        balances.put(BTC, BalanceDTO.builder().available(new BigDecimal("2")).build());
        balances.put(ETH, BalanceDTO.builder().available(new BigDecimal("10")).build());
        balances.put(USDT, BalanceDTO.builder().available(new BigDecimal("2000")).build());
        AccountDTO account03 = AccountDTO.builder().accountId("03").name("trade").balances(balances).build();
        accounts.put("03", account03);
        UserDTO user03 = UserDTO.builder().accounts(accounts).build();
        balances.clear();
        accounts.clear();

        // Mock replies.
        given(userService.getUser()).willReturn(Optional.of(user01), Optional.of(user02), Optional.of(user03));
        return userService;
    }

    @Bean
    @Primary
    public MarketService marketService() {
        MarketService service = mock(MarketService.class);
        given(service.getTicker(any())).willReturn(Optional.empty());
        return service;
    }

    @SuppressWarnings("unchecked")
    @Bean
    @Primary
    public TradeService tradeService() {
        // Creates the mock.
        TradeService tradeService = mock(TradeService.class);
        final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

        // =============================================================================================================
        // Loading strategy.
        StrategyDTO strategyDTO = StrategyDTO.builder().id(1L).strategyId("01").build();

        // Order ORDER_000001.
        OrderDTO order01 = OrderDTO.builder()
                .orderId("ORDER_000001")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
                .leverage("leverage1")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
                .userReference("MY_REF_1")
                .timestamp(createDate(1))
                .build();

        // Order ORDER_000002.
        OrderDTO order02 = OrderDTO.builder()
                .orderId("ORDER_000002")
                .type(BID)
                .strategy(strategyDTO)
                .currencyPair(cp2)
                .amount(new CurrencyAmountDTO("11", cp2.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("13", cp2.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("15", cp2.getQuoteCurrency()))
                .leverage("leverage2")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("12", cp2.getBaseCurrency()))
                .userReference("MY_REF_2")
                .timestamp(createDate(2))
                .build();

        // Order ORDER_000003.
        OrderDTO order03 = OrderDTO.builder()
                .orderId("ORDER_000003")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
                .leverage("leverage1")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
                .userReference("MY_REF_1")
                .timestamp(createDate(1))
                .build();

        Set<OrderDTO> reply01 = new LinkedHashSet<>();
        reply01.add(order01);
        reply01.add(order02);
        reply01.add(order03);

        // =========================================================================================================
        // Second reply.
        // Order ORDER_000003 : the amount changed.
        // Order ORDER_000004 : new order.

        // Order ORDER_000001.
        OrderDTO order04 = OrderDTO.builder()
                .orderId("ORDER_000001")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
                .leverage("leverage1")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
                .userReference("MY_REF_1")
                .timestamp(createDate(1))
                .build();

        // Order ORDER_000002.
        OrderDTO order05 = OrderDTO.builder()
                .orderId("ORDER_000002")
                .type(BID)
                .strategy(strategyDTO)
                .currencyPair(cp2)
                .amount(new CurrencyAmountDTO("11", cp2.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("13", cp2.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("15", cp2.getQuoteCurrency()))
                .leverage("leverage2")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("12", cp2.getBaseCurrency()))
                .userReference("MY_REF_2")
                .timestamp(createDate(2))
                .build();

        // Order ORDER_000003 : the amount changed.
        OrderDTO order06 = OrderDTO.builder()
                .orderId("ORDER_000003")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
                .leverage("leverage1")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
                .userReference("MY_REF_1")
                .timestamp(createDate(1))
                .build();

        // Order ORDER_000004 : new order.
        OrderDTO order07 = OrderDTO.builder()
                .orderId("ORDER_000004")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
                .leverage("leverage1")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
                .userReference("MY_REF_1")
                .timestamp(createDate(1))
                .build();

        Set<OrderDTO> reply02 = new LinkedHashSet<>();
        reply02.add(order04);
        reply02.add(order05);
        reply02.add(order06);
        reply02.add(order07);

        // =========================================================================================================
        // Second reply.
        // Order ORDER_000002 : average prince changed.
        // Order ORDER_000004 : leverage changed.

        // Order ORDER_000001.
        OrderDTO order08 = OrderDTO.builder()
                .orderId("ORDER_000001")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
                .leverage("leverage1")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
                .userReference("MY_REF_1")
                .timestamp(createDate(1))
                .build();

        // Order ORDER_000002 : average price changed.
        OrderDTO order09 = OrderDTO.builder()
                .orderId("ORDER_000002")
                .type(BID)
                .strategy(strategyDTO)
                .currencyPair(cp2)
                .amount(new CurrencyAmountDTO("11", cp2.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("1", cp2.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("15", cp2.getQuoteCurrency()))
                .leverage("leverage2")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("12", cp2.getBaseCurrency()))
                .userReference("MY_REF_2")
                .timestamp(createDate(2))
                .build();

        // Order ORDER_000003.
        OrderDTO order10 = OrderDTO.builder()
                .orderId("ORDER_000003")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
                .leverage("leverage1")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
                .userReference("MY_REF_1")
                .timestamp(createDate(1))
                .build();

        // Order ORDER_000004 : leverage changed.
        OrderDTO order11 = OrderDTO.builder()
                .orderId("ORDER_000004")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("1", cp1.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()))
                .leverage("leverage2")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("2", cp1.getBaseCurrency()))
                .userReference("MY_REF_1")
                .timestamp(createDate(1))
                .build();

        Set<OrderDTO> reply03 = new LinkedHashSet<>();
        reply03.add(order08);
        reply03.add(order09);
        reply03.add(order10);
        reply03.add(order11);

        // Creating the mock.
        given(tradeService.getOrders())
                .willReturn(reply01,
                        new LinkedHashSet<>(),
                        reply02,
                        reply03);
        given(tradeService.getTrades()).willReturn(Collections.emptySet());
        return tradeService;
    }

}
