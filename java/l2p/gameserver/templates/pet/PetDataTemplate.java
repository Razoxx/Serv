package l2p.gameserver.templates.pet;

import gnu.trove.map.hash.TIntObjectHashMap;

public class PetDataTemplate {

    private final int _npcId;
    private final int _controlItemId;
    private final int[] _syncLevels;
    private final TIntObjectHashMap<PetLvlData> _lvlData;
    private int _minLvl = 0;
    private int _maxLvl = 0;

    public PetDataTemplate(int npcId, int controlItemId, int[] syncLevels) {
        _npcId = npcId;
        _controlItemId = controlItemId;
        _syncLevels = syncLevels;
        _lvlData = new TIntObjectHashMap<PetLvlData>();
    }

    public int getNpcId() {
        return _npcId;
    }

    public int getControlItemId() {
        return _controlItemId;
    }

    public void addLvlData(int lvl, PetLvlData lvlData) {
        _lvlData.put(lvl, lvlData);
    }

    public PetLvlData getLvlData(int level) {
        return (PetLvlData) _lvlData.get(Math.max(_minLvl, Math.min(_maxLvl, level)));
    }

    public int getMaxMeal(int level) {
        return getLvlData(level).getMaxMeal();
    }

    public long getExp(int level) {
        return getLvlData(level).getExp();
    }

    public int getExpType(int level) {
        return getLvlData(level).getExpType();
    }

    public int getBattleMealConsume(int level) {
        return getLvlData(level).getBattleMealConsume();
    }

    public int getNormalMealConsume(int level) {
        return getLvlData(level).getNormalMealConsume();
    }

    public double getPAtk(int level) {
        return getLvlData(level).getPAtk();
    }

    public double getPDef(int level) {
        return getLvlData(level).getPDef();
    }

    public double getMAtk(int level) {
        return getLvlData(level).getMAtk();
    }

    public double getMDef(int level) {
        return getLvlData(level).getMDef();
    }

    public double getHP(int level) {
        return getLvlData(level).getHP();
    }

    public double getMP(int level) {
        return getLvlData(level).getMP();
    }

    public double getHPRegen(int level) {
        return getLvlData(level).getHPRegen();
    }

    public double getMPRegen(int level) {
        return getLvlData(level).getMPRegen();
    }

    public int[] getFood(int level) {
        return getLvlData(level).getFood();
    }

    public int getHungryLimit(int level) {
        return getLvlData(level).getHungryLimit();
    }

    public int getSoulshotCount(int level) {
        return getLvlData(level).getSoulshotCount();
    }

    public int getSpiritshotCount(int level) {
        return getLvlData(level).getSpiritshotCount();
    }

    public int getMaxLoad(int level) {
        return getLvlData(level).getMaxLoad();
    }

    public int getAccuracy(int level) {
        return getLvlData(level).getAccuracy();
    }

    public int getCriticalHit(int level) {
        return getLvlData(level).getCriticalHit();
    }

    public int getEvasionRate(int level) {
        return getLvlData(level).getEvasionRate();
    }

    public void setMinLvl(int lvl) {
        _minLvl = lvl;
    }

    public int getMinLvl() {
        return _minLvl;
    }

    public void setMaxLvl(int lvl) {
        _maxLvl = lvl;
    }

    public int getMaxLvl() {
        return _maxLvl;
    }

    public int getFormId(int level) {
        for (int i = 0; i < _syncLevels.length; i++) {
            if (level >= _syncLevels[i]) {
                return i + 1;
            }
        }
        return 0;
    }
}
