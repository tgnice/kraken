package org.krakenapps.siem.analyzer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.krakenapps.event.api.Event;
import org.krakenapps.event.api.EventDispatcher;
import org.krakenapps.event.api.EventSeverity;
import org.krakenapps.siem.LogServer;
import org.krakenapps.siem.NormalizedLog;
import org.krakenapps.siem.NormalizedLogListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(name = "siem-ips-log-analyzer")
public class IpsLogAnalyzer implements NormalizedLogListener {
	private final Logger logger = LoggerFactory.getLogger(IpsLogAnalyzer.class.getName());

	@Requires
	private LogServer logServer;

	@Requires
	private EventDispatcher eventDispatcher;

	private ConcurrentMap<AttackKey, AttackEvent> attacks;

	@Validate
	public void start() {
		attacks = new ConcurrentHashMap<AttackKey, AttackEvent>();
		logServer.addNormalizedLogListener("ips", this);
	}

	@Invalidate
	public void stop() {
		if (logServer != null)
			logServer.removeNormalizedLogListener("ips", this);
	}

	@Override
	public void onLog(NormalizedLog log) {
		logger.trace("kraken log api: received ips log [{}]", log);

		try {
			AttackKey key = new AttackKey(log.getString("src"), log.getString("dst"), log.getString("rule"));

			Event event = new Event();
			event.setOrgDomain(log.getOrgDomain());
			event.setCategory("attack");
			event.setFirstSeen(log.getDate("date"));
			event.setLastSeen(log.getDate("date"));
			event.setSourceIp(InetAddress.getByName(log.getString("src_ip")));
			event.setSourcePort(log.getInteger("src_port"));
			event.setDestinationIp(InetAddress.getByName(log.getString("dst_ip")));
			event.setDestinationPort(log.getInteger("dst_port"));
			event.setSeverity(EventSeverity.values()[log.getInteger("severity")]);
			event.setRule(log.getString("rule"));
			event.setDetail(log.getString("detail"));
			event.setCount(log.getInteger("count"));

			// save until ack
			AttackEvent old = attacks.putIfAbsent(key, new AttackEvent(event));
			if (old != null) {
				event.setKey(old.getEvent().getKey());
				event.setCount(old.getCount().addAndGet(event.getCount()));
			}

			eventDispatcher.dispatch(event);
		} catch (UnknownHostException e) {
		}
	}

}
