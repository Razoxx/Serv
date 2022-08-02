package l2p.commons.net.nio.impl;

@SuppressWarnings("rawtypes")
public interface IClientFactory<T extends MMOClient> {

    public T create(MMOConnection<T> con);
}