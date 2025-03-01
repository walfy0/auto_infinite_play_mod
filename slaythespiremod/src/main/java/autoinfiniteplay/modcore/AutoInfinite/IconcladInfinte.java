package autoinfiniteplay.modcore.AutoInfinite;

import autoinfiniteplay.modcore.InfiniteCondition;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.colorless.Finesse;
import com.megacrit.cardcrawl.cards.colorless.FlashOfSteel;
import com.megacrit.cardcrawl.cards.red.PommelStrike;
import com.megacrit.cardcrawl.cards.red.ShrugItOff;
import com.megacrit.cardcrawl.characters.Ironclad;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Sundial;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static autoinfiniteplay.modcore.CommonUtil.print;

public class IconcladInfinte {
    public static final Logger logger = LogManager.getLogger(IconcladInfinte.class);

    // 剑耸无限
    static InfiniteCondition PS = new InfiniteCondition(
            Arrays.asList(PommelStrike.ID, ShrugItOff.ID),
            Arrays.asList(true, false),
            Arrays.asList(Sundial.ID)
    );

    // 亮剑妙计无限
    static InfiniteCondition FF = new InfiniteCondition(
            Arrays.asList(FlashOfSteel.ID, Finesse.ID),
            Arrays.asList(false, false),
            Arrays.asList()
    );

    public static InfiniteCondition isInfinite() {
        if (!(AbstractDungeon.player instanceof Ironclad)) {
            return null;
        }
        List<InfiniteCondition> conditions = new ArrayList<>();
        conditions.add(PS);
        conditions.add(FF);
        for (InfiniteCondition condition : conditions) {
            CardGroup hand = AbstractDungeon.player.hand;
            CardGroup discard = AbstractDungeon.player.discardPile;

            int discardPileSize = AbstractDungeon.player.discardPile.size();
            int drawPileSize = AbstractDungeon.player.drawPile.size();
            int cardInUseNum = discardPileSize + drawPileSize;
            if (AbstractDungeon.player.cardInUse != null) cardInUseNum++;

            boolean hasRelics = true;
            for (String relic : condition.Relics) {
                if (!AbstractDungeon.player.relics.stream().anyMatch(r -> {
                    return Objects.equals(r.relicId, relic);
                })) hasRelics = false;
            }
            String A = condition.Cards.get(0);
            String B = condition.Cards.get(1);

            AbstractCard cardInUse = AbstractDungeon.player.cardInUse;
            if (hasRelics && cardInUseNum <= 1 && allInHand(condition, hand)) {
                return condition;
            }
            if (hasRelics && cardInUseNum <= 1
                    && (allInHand(condition, hand) || aInHand(condition, hand, discard, cardInUse) || (bInHand(condition, hand, discard, cardInUse)))) {
                return condition;
            }
            if (!hasRelics) logger.info("no relics");
            logger.info(String.valueOf(null != hand.findCardById(A)) + (null != hand.findCardById(B)) +
                    (null != discard.findCardById(A)) + (null != discard.findCardById(B)) +
                    (AbstractDungeon.player.cardInUse));
            print();
        }
        return null;
    }

    public static boolean allInHand(InfiniteCondition condition, CardGroup hand) {
        String A = condition.Cards.get(0);
        String B = condition.Cards.get(1);
        if (null != hand.findCardById(A) && null != hand.findCardById(B)) {
            if (condition.NeedUpgrades.get(0) && !hand.findCardById(A).upgraded)return false;
            if (condition.NeedUpgrades.get(1) && !hand.findCardById(B).upgraded)return false;
            return true;
        }
        return false;
    }

    public static boolean aInHand(InfiniteCondition condition, CardGroup hand, CardGroup discard, AbstractCard cardInUse) {
        String A = condition.Cards.get(0);
        String B = condition.Cards.get(1);
        if (null != hand.findCardById(A) && null != cardInUseOrDiscard(discard, cardInUse, B)) {
            if (condition.NeedUpgrades.get(0) && !hand.findCardById(A).upgraded)return false;
            if (condition.NeedUpgrades.get(1) && !cardInUseOrDiscard(discard, cardInUse, B).upgraded)return false;
            return true;
        }
        return false;
    }

    public static boolean bInHand(InfiniteCondition condition, CardGroup hand, CardGroup discard, AbstractCard cardInUse) {
        String A = condition.Cards.get(0);
        String B = condition.Cards.get(1);
        if (null != hand.findCardById(B) && null != cardInUseOrDiscard(discard, cardInUse, A)) {
            if (condition.NeedUpgrades.get(0) && !cardInUseOrDiscard(discard, cardInUse, A).upgraded)return false;
            if (condition.NeedUpgrades.get(1) && !hand.findCardById(B).upgraded)return false;
            return true;
        }
        return false;
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

    public static AbstractCard play(InfiniteCondition condition) {
        CardGroup hand = AbstractDungeon.player.hand;
        String A = condition.Cards.get(0);
        String B = condition.Cards.get(1);
        AbstractCard ACard = findCardById(hand, A, condition.NeedUpgrades.get(0));
        AbstractCard BCard = findCardById(hand, B, condition.NeedUpgrades.get(1));
        if (null != ACard) {
            logger.info("[IconcladInfinte]start to play " + ACard.name);
            return ACard;
//            return new CardQueueItem(ACard, true, ACard.energyOnUse, true, false);
        } else if (null != BCard) {
            logger.info("[IconcladInfinte]start to play" +  BCard.name);
            return BCard;
//            return new CardQueueItem(BCard, true, BCard.energyOnUse, true, false);
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
