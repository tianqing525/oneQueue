package com.eyu.onequeue.store.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.eyu.onequeue.QMConfig;
import com.eyu.onequeue.protocol.model.QConsume;
import com.eyu.onequeue.store.model.IQMStore;
import com.eyu.onequeue.store.model.IQMStoreService;
import com.eyu.onequeue.store.model.IStoreMBean;
import com.eyu.onequeue.store.model.QQuery;
import com.eyu.onequeue.util.PoolUtil;

/***
 * 提供对其它模块服务
 * 
 * @author solq
 */
@Service
public class QMStoreService implements IQMStoreService, IStoreMBean, InitializingBean {
    private final static Logger logger = LoggerFactory.getLogger(QMStoreService.class);
    private Map<String, IQMStore> stores = new HashMap<>();
    private int useStoreCount = 0;
    private static ExecutorService pool = PoolUtil.createPool(QMConfig.getInstance().POOL_STORE_CORE, 60 * 2, "fileStore");
    private volatile boolean closed = false;

    private Thread monitor = new Thread(new Runnable() {

	@Override
	public void run() {
	    while (!closed) {
		try {
		    Thread.sleep(QMConfig.getInstance().STORE_FILE_PERSIST_INTERVAL);
		} catch (InterruptedException e) {
		}
		if (!closed) {
		    break;
		}
		persist();
	    }
	}
    }, "qm message persist monitor");

    @Override
    public void afterPropertiesSet() throws Exception {
	monitor.setDaemon(true);
	monitor.start();
    }

    @Override
    public void save(String topic, Object... messages) {
	IQMStore store = getStore(topic);
	store.save(messages);
    }

    @Override
    public QConsume query(QQuery query) {
	IQMStore store = getStore(query.getTopic());
	return store.query(query);
    }

    @Override
    public void persist() {
	Map<String, IQMStore> stores = getStores();
	for (IQMStore store : stores.values()) {
	    execute(() -> store.persist());
	}
    }

    @Override
    public void close() {
	if (closed) {
	    return;
	}
	closed = true;
 
	Map<String, IQMStore> stores = getStores();
	for (IQMStore store : stores.values()) {
	    execute(() -> store.close());
	}
	PoolUtil.shutdown(pool, 60 * 2);
    }

    @Override
    public int getUseStoreCount() {
	return useStoreCount;
    }

    /////////////////////////////////////////////////////////

    private void execute(Runnable task) {
	try {
	    pool.submit(task);
	} catch (Exception e) {
	    logger.error("PushMonitor", e);
	}
    }

    private synchronized Map<String, IQMStore> getStores() {
	return new HashMap<>(this.stores);
    }

    private IQMStore getStore(String topic) {
	IQMStore ret = stores.get(topic);
	if (ret != null) {
	    return ret;
	}
	synchronized (this) {
	    ret = stores.get(topic);
	    if (ret != null) {
		return ret;
	    }
	    ret = FileQMStore.of(topic, FileIndexer.of(topic));
	    stores.put(topic, ret);
	    useStoreCount++;
	}
	return ret;
    }

}