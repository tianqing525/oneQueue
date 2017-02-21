package com.eyu.onequeue.rpc.model;

import com.eyu.onequeue.callback.model.IQCallback;
import com.eyu.onequeue.protocol.model.QRpc;
import com.eyu.onequeue.socket.model.QNode;
import com.eyu.onequeue.socket.service.QNodeFactory;

/**
 * @author solq
 * @version 2014-3-6 上午11:20:38
 */
public class RpcContext {
    private QNode[] nodes;
    private long[] ids;
    private IQCallback<?> cb;

    public static RpcContext of(QNode... nodes) {
	RpcContext ret = new RpcContext();
	ret.nodes = nodes;
	return ret;
    }
    public static RpcContext of(long... ids) {
	RpcContext ret = new RpcContext();
	ret.ids = ids;
	return ret;
    }

    // get setter
    public QNode[] getNodes() {
	return nodes;
    }

    public IQCallback<?> getCb() {
	return cb;
    }

    public void send(QRpc rpc) {
 /*	for(long id : ids){
	     QNodeFactory.get(id);
	} */
    }

}
