package autoinfiniteplay.modcore.AutoInfinite;

import autoinfiniteplay.modcore.InfiniteCondition;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.purple.*;
import com.megacrit.cardcrawl.characters.Watcher;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.watcher.RushdownPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.VioletLotus;
import com.megacrit.cardcrawl.stances.CalmStance;
import com.megacrit.cardcrawl.stances.WrathStance;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static autoinfiniteplay.modcore.CommonUtil.print;

public class WatcherInfinte {
    public static final Logger logger = LogManager.getLogger(IconcladInfinte.class);

    // 基础红蓝无限
    static InfiniteCondition EV = new InfiniteCondition(
            Arrays.asList(Eruption.ID, Vigilance.ID),
            Arrays.asList(true, false),
            Arrays.asList()
    );

    // 2费红蓝无限
    static InfiniteCondition TI = new InfiniteCondition(
            Arrays.asList(Tantrum.ID, InnerPeace.ID),
            Arrays.asList(false, false),
            Arrays.asList()
    );

    // 2费红蓝无限
    static InfiniteCondition EI = new InfiniteCondition(
            Arrays.asList(Eruption.ID, InnerPeace.ID),
            Arrays.asList(false, false),
            Arrays.asList()
    );


    // 3费红蓝无限
    static InfiniteCondition TV = new InfiniteCondition(
            Arrays.asList(Tantrum.ID, Vigilance.ID),
            Arrays.asList(false, false),
            Arrays.asList()
    );

    public static InfiniteCondition isInfinite(String stanceId) {
        if (!(AbstractDungeon.player instanceof Watcher)) {
            return null;
        }
        List<InfiniteCondition> conditions = new ArrayList<>();
        conditions.add(EV);
        conditions.add(TI);
        conditions.add(EI);
        conditions.add(TV);
        for (InfiniteCondition condition : conditions) {
            if (Objects.equals(stanceId, CalmStance.STANCE_ID)) {
                if (checkCalm(condition)) {
                    return condition;
                }
            } else if (Objects.equals(stanceId, WrathStance.STANCE_ID)) {
                if (checkWrath(condition)) {
                    return condition;
                }
            }
            return null;
        }
        return null;
    }

    public static boolean checkCalm(InfiniteCondition condition) {
        if (!checkCardNum())return false;

        CardGroup hand = AbstractDungeon.player.hand;

        // 有红卡
        String A = condition.Cards.get(0);
        String B = condition.Cards.get(1);
        AbstractCard ACard = hand.findCardById(A);
        AbstractCard BCard = cardInUseOrDiscard(AbstractDungeon.player.discardPile, AbstractDungeon.player.cardInUse, B);
        if (null == ACard){
            logger.info("not find wrath card in hand");
            return false;
        }
        if (null == BCard){
            logger.info("not find calm card in discard or in use, " + hand.findCardById(B));
            return false;
        }

        // 能量够
        int playEnergy = 2;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (Objects.equals(relic.relicId, VioletLotus.ID)) {
                playEnergy ++;
            }
        }
        int needEnergy = ACard.cost + BCard.cost;
        if (playEnergy < needEnergy || EnergyPanel.totalCount < ACard.cost){
            logger.info("A: " + ACard.cost + ",B:" + BCard.cost +
                    ", need energy: "+ needEnergy + ", play energy: " + playEnergy + ", total: "+ EnergyPanel.totalCount);
            return false;
        }
        return true;
    }

    public static boolean checkWrath(InfiniteCondition condition) {
        if (!checkCardNum()) return false;

        CardGroup hand = AbstractDungeon.player.hand;
        CardGroup discard = AbstractDungeon.player.discardPile;
        CardGroup draw = AbstractDungeon.player.drawPile;

        // 检查平静牌在不在场上
        String A = condition.Cards.get(0);
        String B = condition.Cards.get(1);
        AbstractCard aCard = cardExist(hand, AbstractDungeon.player.drawPile, AbstractDungeon.player.discardPile, AbstractDungeon.player.cardInUse, A);
        AbstractCard bCard = cardExist(hand, AbstractDungeon.player.drawPile, AbstractDungeon.player.discardPile, null, B);
        if (aCard == null || bCard == null) {
            logger.info("can't find the wrath or calm card: " + aCard + ", "+ bCard);
            return false;
        }

        // 能量够
        int playEnergy = 2;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (Objects.equals(relic.relicId, VioletLotus.ID)) {
                playEnergy ++;
            }
        }
        int needEnergy = aCard.cost + bCard.cost;
        if (playEnergy < needEnergy){
            logger.info("A: " + aCard.cost + ",B:" + bCard.cost +
                    ", need energy: "+ needEnergy + ", play energy: " + playEnergy + ", total: "+ EnergyPanel.totalCount);
            return false;
        }
        return true;
    }

    public static boolean checkCardNum() {
        CardGroup hand = AbstractDungeon.player.hand;
        int drawNum = 0;
        for (AbstractPower power: AbstractDungeon.player.powers) {
            if(Objects.equals(power.ID, RushdownPower.POWER_ID)) {
                drawNum += 2;
            }
        }
        logger.info("draw num is: " + drawNum);

        // 抽牌数大于弃牌+抽牌堆的数量
        int discardPileSize = AbstractDungeon.player.discardPile.size();
        int drawPileSize = AbstractDungeon.player.drawPile.size();
        int cardInUseNum = discardPileSize + drawPileSize;
        if (AbstractDungeon.player.cardInUse != null) cardInUseNum++;
        if (drawNum < cardInUseNum){
            logger.info("draw num is: " + drawNum + ", card in use num is: " + cardInUseNum);
            return false;
        }

        // 卡总数小于等于10
        if (cardInUseNum + hand.size() > 10) {
            logger.info("hand num is: " + hand.size() + ", card in use num is: " + cardInUseNum);
            return false;
        }
        return true;
    }

    public static AbstractCard cardExist(CardGroup hand, CardGroup draw, CardGroup discard, AbstractCard cardInUse, String cardId) {
        if (null != hand.findCardById(cardId)) {
            return hand.findCardById(cardId);
        }
        if (null != draw.findCardById(cardId)) {
            return draw.findCardById(cardId);
        }
        if (null != discard.findCardById(cardId)) {
            return discard.findCardById(cardId);
        }
        if (cardInUse != null && Objects.equals(cardInUse.cardID, cardId)) {
            return cardInUse;
        }
        return null;
    }

    public static AbstractCard cardInUseOrDiscard(CardGroup discard, AbstractCard cardInUse, String cardId) {
        if (null != discard.findCardById(cardId)) {
            return discard.findCardById(cardId);
        }
        if (cardInUse != null && Objects.equals(cardInUse.cardID, cardId)) {
            return cardInUse;
        }
        return null;
    }

    public static AbstractCard play(InfiniteCondition condition, String stanceId) {
        CardGroup hand = AbstractDungeon.player.hand;
        String A = condition.Cards.get(0);
        String B = condition.Cards.get(1);
        AbstractCard ACard = findCardById(hand, A, condition.NeedUpgrades.get(0));
        AbstractCard BCard = findCardById(hand, B, condition.NeedUpgrades.get(1));
        if (BCard == null) {
            BCard = findCardById(AbstractDungeon.player.discardPile, B, condition.NeedUpgrades.get(1));
        }
        if (BCard == null) {
            BCard = findCardById(AbstractDungeon.player.drawPile, B, condition.NeedUpgrades.get(1));
        }
        if (null != ACard && Objects.equals(stanceId, CalmStance.STANCE_ID)) {
            logger.info("[WatcherInfinte]start to play Wrath " + ACard.name);
            return ACard;
//            return new CardQueueItem(ACard, true, ACard.energyOnUse, true, false);
        } else {
            logger.info("can't find wrath card or stance is wrong: " + ACard + ", " + stanceId);
        }
        if (null != BCard && Objects.equals(stanceId, WrathStance.STANCE_ID)) {
            logger.info("[WatcherInfinte]start to play Calm" +  BCard.name);
            return BCard;
//            return new CardQueueItem(BCard, true, BCard.energyOnUse, true, false);
        } else {
            logger.info("can't find calm card or stance is wrong: " + BCard + ", " + stanceId);
            print();
        }
        return null;
    }

    public static AbstractCard findCardById(CardGroup group, String id, boolean needUpgrade) {
        if (needUpgrade) {
            for(AbstractCard c : group.group) {
                if (c.cardID.equals(id) && c.upgraded) {
                    return c;
                }
            }
        } else {
            for(AbstractCard c : group.group) {
                if (c.cardID.equals(id)) {
                    return c;
                }
            }
        }
        return null;
    }
}
