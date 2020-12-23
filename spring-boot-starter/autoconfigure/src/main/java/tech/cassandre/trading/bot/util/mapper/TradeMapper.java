package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.dto.trade.UserTrade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Trade mapper.
 */
@Mapper(uses = {TypeMapper.class, CurrencyMapper.class})
public interface TradeMapper {

    // =================================================================================================================
    // XChange to DTO.

    /**
     * Map UserTrade to TradeDTO.
     *
     * @param source User trade
     * @return TradeDTO
     */
    @Mapping(source = "source", target = "amount", qualifiedByName = "mapUserTradeToTradeDTOAmount")
    @Mapping(source = "source", target = "price", qualifiedByName = "mapUserTradeToTradeDTOPrice")
    @Mapping(source = "source", target = "fee", qualifiedByName = "mapUserTradeToTradeDTOFee")
    TradeDTO mapToTradeDTO(UserTrade source);

    @Named("mapUserTradeToTradeDTOAmount")
    default CurrencyAmountDTO mapUserTradeToTradeDTOAmount(UserTrade source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        return new CurrencyAmountDTO(source.getOriginalAmount(), cp.getBaseCurrency());
    }

    @Named("mapUserTradeToTradeDTOPrice")
    default CurrencyAmountDTO mapUserTradeToTradeDTOPrice(UserTrade source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        return new CurrencyAmountDTO(source.getPrice(), cp.getQuoteCurrency());
    }

    @Named("mapUserTradeToTradeDTOFee")
    default CurrencyAmountDTO mapUserTradeToTradeDTOFee(UserTrade source) {
        if (source.getFeeAmount() != null && source.getFeeCurrency() != null) {
            return new CurrencyAmountDTO(source.getFeeAmount(), new CurrencyDTO(source.getFeeCurrency().toString()));
        } else {
            return new CurrencyAmountDTO();
        }
    }

    // =================================================================================================================
    // DTO to domain.

    /**
     * Map TradeDTO to Trade.
     *
     * @param source TradeDTO
     * @return Trade
     */
    @Mapping(source = "amount.value", target = "amount")
    @Mapping(source = "price.value", target = "price")
    @Mapping(source = "fee.value", target = "feeAmount")
    @Mapping(source = "fee.currency", target = "feeCurrency")
    Trade mapToTrade(TradeDTO source);

    // =================================================================================================================
    // Domain to DTO.

    /**
     * Map Trade to tradeDTO.
     *
     * @param source trade
     * @return tradeDRO
     */
    @Mapping(source = "source", target = "amount", qualifiedByName = "mapTradeToTradeDTOAmount")
    @Mapping(source = "source", target = "price", qualifiedByName = "mapTradeToTradeDTOPrice")
    @Mapping(source = "source", target = "fee", qualifiedByName = "mapTradeToTradeDTOFee")
    TradeDTO mapToTradeDTO(Trade source);

    @Named("mapTradeToTradeDTOAmount")
    default CurrencyAmountDTO mapTradeToTradeDTOAmount(Trade source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        return new CurrencyAmountDTO(source.getAmount(), cp.getBaseCurrency());
    }

    @Named("mapTradeToTradeDTOPrice")
    default CurrencyAmountDTO mapTradeToTradeDTOPrice(Trade source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        return new CurrencyAmountDTO(source.getPrice(), cp.getQuoteCurrency());
    }

    @Named("mapTradeToTradeDTOFee")
    default CurrencyAmountDTO mapTradeToTradeDTOFee(Trade source) {
        // TODO Find why fee currency is null.
        if (source.getFeeAmount() != null && source.getFeeCurrency() != null) {
            return new CurrencyAmountDTO(source.getFeeAmount(), new CurrencyDTO(source.getFeeCurrency()));
        } else {
            return new CurrencyAmountDTO();
        }
    }

    // =================================================================================================================
    // Util.

    /**
     * Map a trade set to a tradeDTO hashmap.
     *
     * @param source Trade set
     * @return TradeDTO hashmap
     */
    default Map<String, TradeDTO> map(Set<Trade> source) {
        Map<String, TradeDTO> results = new LinkedHashMap<>();
        source.forEach(trade -> results.put(trade.getId(), mapToTradeDTO(trade)));
        return results;
    }

}