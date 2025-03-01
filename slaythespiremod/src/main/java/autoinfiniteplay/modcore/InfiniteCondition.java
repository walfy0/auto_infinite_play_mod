package autoinfiniteplay.modcore;

import java.util.List;

public class InfiniteCondition {
    public List<String> Cards;
    public List<Boolean> NeedUpgrades;
    public List<String> Relics;
    public int NeedEnergy;

    public InfiniteCondition(List<String> _cards, List<Boolean> _needUpgrades, List<String> _relics) {
        Cards = _cards;
        NeedUpgrades = _needUpgrades;
        Relics = _relics;
    }

    public InfiniteCondition(List<String> _cards, List<Boolean> _needUpgrades, List<String> _relics, int _needEnergy) {
        Cards = _cards;
        NeedUpgrades = _needUpgrades;
        Relics = _relics;
        NeedEnergy = _needEnergy;
    }
}