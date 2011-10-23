/*
 * Copyright 2011 Future Systems
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.krakenapps.logdb.query;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.krakenapps.logdb.query.command.Result;
import org.krakenapps.logdb.LogQuery;
import org.krakenapps.logdb.LogQueryCallback;
import org.krakenapps.logdb.LogQueryCommand;
import org.krakenapps.logdb.LogQueryService;
import org.krakenapps.logstorage.LogStorage;
import org.krakenapps.logstorage.LogTableRegistry;
import org.krakenapps.logdb.LogQueryCommand.Status;
import org.krakenapps.logdb.LogTimelineCallback;
import org.krakenapps.logdb.query.LogQueryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogQueryImpl implements LogQuery {
	private Logger logger = LoggerFactory.getLogger(LogQueryImpl.class);
	private static AtomicInteger nextId = new AtomicInteger(1);
	private final int id = nextId.getAndIncrement();
	private String queryString;
	private List<LogQueryCommand> commands = new ArrayList<LogQueryCommand>();
	private Date lastStarted;
	private Result result;
	private Set<LogQueryCallback> logQueryCallbacks = new HashSet<LogQueryCallback>();
	private Set<LogTimelineCallback> timelineCallbacks = new HashSet<LogTimelineCallback>();

	public LogQueryImpl(LogQueryService service, LogStorage logStorage, LogTableRegistry tableRegistry, String query) {
		this.queryString = query;

		for (String q : queryString.split("\\|")) {
			q = q.trim();
			try {
				commands.add(LogQueryCommand.createCommand(service, this, logStorage, tableRegistry, q));
			} catch (ParseException e) {
				throw new IllegalArgumentException("invalid query command: " + q);
			}
		}

		for (int i = 0; i < commands.size() - 1; i++)
			commands.get(i).setNextCommand(commands.get(i + 1));

		commands.get(commands.size() - 1).setCallbackTimeline(true);
		for (int i = 1; i < commands.size(); i++) {
			LogQueryCommand command = commands.get(i);
			if (command.isReducer()) {
				commands.get(commands.size() - 1).setCallbackTimeline(false);
				commands.get(i - 1).setCallbackTimeline(true);
				break;
			}
		}
	}

	@Override
	public void run() {
		if (!isEnd())
			throw new IllegalStateException("already running");

		lastStarted = new Date();
		if (commands.size() <= 0)
			return;

		try {
			result = new Result();
			commands.get(commands.size() - 1).setNextCommand(result);
			for (LogQueryCallback callback : logQueryCallbacks)
				result.registerCallback(callback);
			logQueryCallbacks.clear();

			logger.trace("kraken logstorage: run query => {}", queryString);
			for (LogQueryCommand command : commands)
				command.init();

			commands.get(0).start();
		} catch (IOException e) {
			logger.error("kraken logstorage: cannot start query", e);
		}
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getQueryString() {
		return queryString;
	}

	@Override
	public boolean isEnd() {
		if (commands.size() == 0)
			return true;
		if (result == null)
			return true;
		if (commands.get(0).getStatus() == Status.Waiting)
			return true;
		return result.getStatus().equals(Status.End);
	}

	@Override
	public void cancel() {
		if (result.getStatus() != Status.End)
			result.eof();
		for (int i = commands.size() - 1; i >= 0; i--) {
			LogQueryCommand command = commands.get(i);
			if (command.getStatus() != Status.End) {
				command.eof();
			}
		}
	}

	@Override
	public Date getLastStarted() {
		return lastStarted;
	}

	@Override
	public List<Map<String, Object>> getResult() {
		if (result != null)
			return result.getResult();
		return null;
	}

	@Override
	public List<Map<String, Object>> getResult(int offset, int limit) {
		if (result != null)
			return result.getResult(offset, limit);
		return null;
	}

	@Override
	public List<LogQueryCommand> getCommands() {
		return commands;
	}

	@Override
	public Set<LogQueryCallback> getLogQueryCallback() {
		return logQueryCallbacks;
	}

	@Override
	public void registerQueryCallback(LogQueryCallback callback) {
		logQueryCallbacks.add(callback);
	}

	@Override
	public void unregisterQueryCallback(LogQueryCallback callback) {
		logQueryCallbacks.add(callback);
	}

	@Override
	public Set<LogTimelineCallback> getTimelineCallbacks() {
		return timelineCallbacks;
	}

	@Override
	public void registerTimelineCallback(LogTimelineCallback callback) {
		timelineCallbacks.add(callback);
	}

	@Override
	public void unregisterTimelineCallback(LogTimelineCallback callback) {
		timelineCallbacks.remove(callback);
	}

	@Override
	public String toString() {
		return "id=" + id + ", query=" + queryString;
	}

}