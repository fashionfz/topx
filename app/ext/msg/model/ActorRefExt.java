package ext.msg.model;

import java.util.HashSet;

import akka.actor.ActorRef;

public class ActorRefExt {
	
	private long millisecond;
	
	private String token;
	
	private HashSet<ActorRef> actorRef;
	

	public ActorRefExt(long millisecond,HashSet<ActorRef> actorRef) {
		super();
		this.millisecond = millisecond;
		this.actorRef = actorRef;
	}

	public long getMillisecond() {
		return millisecond;
	}

	public void setMillisecond(long millisecond) {
		this.millisecond = millisecond;
	}

	public HashSet<ActorRef> getActorRef() {
		return actorRef;
	}

	public void setActorRef(HashSet<ActorRef> actorRef) {
		this.actorRef = actorRef;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

}
