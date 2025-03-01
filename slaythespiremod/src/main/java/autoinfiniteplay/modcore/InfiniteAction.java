package autoinfiniteplay.modcore;

import autoinfiniteplay.modcore.AutoInfinite.WatcherInfinte;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.Ironclad;
import com.megacrit.cardcrawl.characters.Watcher;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import autoinfiniteplay.modcore.AutoInfinite.IconcladInfinte;


public class InfiniteAction extends AbstractGameAction {
    public static final Logger logger = LogManager.getLogger(InfiniteAction.class);

    public String stanceId;

    public InfiniteAction() {}

    public InfiniteAction(String _stanceId) {
        stanceId = _stanceId;
    }

    @Override
    public void update() {
        if (AbstractDungeon.player instanceof Ironclad) {
            InfiniteCondition condition = IconcladInfinte.isInfinite();
            if (condition != null) {
                logger.info("[InfiniteAction]start Ironclad infinite play check ok!");
                AbstractCard card = IconcladInfinte.play(condition);
                if (card != null) {
//                    AbstractDungeon.actionManager.addCardQueueItem(queue, false);
                    AbstractDungeon.actionManager.addToBottom(new NewQueueCardAction(card, true, true, false));
                }
            }
        } else if (AbstractDungeon.player instanceof Watcher) {
            InfiniteCondition condition = WatcherInfinte.isInfinite(stanceId);
            if (condition != null) {
                logger.info("[InfiniteAction]start Watcher infinite play check ok!");
                AbstractCard card = WatcherInfinte.play(condition, stanceId);
                if (card != null) {
//                    AbstractDungeon.actionManager.addCardQueueItem(queue, false);
                    AbstractDungeon.actionManager.addToBottom(new NewQueueCardAction(card, true, true, false));
                }
            }
        }
        this.isDone = true;
    }

    public static void initialize() {
        new InfiniteAction();
    }
}
