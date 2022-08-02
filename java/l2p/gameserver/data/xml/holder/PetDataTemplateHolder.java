package l2p.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2p.commons.data.xml.AbstractHolder;
import l2p.gameserver.templates.pet.PetDataTemplate;

public final class PetDataTemplateHolder extends AbstractHolder
{
	private static final TIntObjectHashMap<PetDataTemplate> _templatesByNpcId = new TIntObjectHashMap<>();
	private static final TIntObjectHashMap<PetDataTemplate> _templatesByItemId = new TIntObjectHashMap<>();
  
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final PetDataTemplateHolder _instance = new PetDataTemplateHolder();
	}

	public static PetDataTemplateHolder getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public void addTemplate(PetDataTemplate template)
	{
		_templatesByNpcId.put(template.getNpcId(), template);
		_templatesByItemId.put(template.getControlItemId(), template);
	}
	
	public PetDataTemplate getTemplateByNpcId(int npcId)
	{
		return (PetDataTemplate)_templatesByNpcId.get(npcId);
	}
	
	public PetDataTemplate getTemplateByItemId(int itemId)
	{
		return (PetDataTemplate)_templatesByItemId.get(itemId);
	}
	
	public boolean isControlItem(int itemId)
	{
		return _templatesByItemId.containsKey(itemId);
	}
	
	public int size()
	{
		return _templatesByNpcId.size();
	}
	
	public void clear()
	{
		_templatesByNpcId.clear();
		_templatesByItemId.clear();
	}
}