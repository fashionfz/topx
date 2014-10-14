package system.vo;

/**
 * @ClassName: CommentVO
 * @Description: 评价与回复VO
 * 
 */
public class CommentVO {

	private Long id;

	/**
	 * 评论星级
	 */
	private Integer level;

	/**
	 * 评价时间
	 */
	private String commentTime;

	/**
	 * 评价用户的用户id
	 */
	private Long commentUserId;

	/**
	 * 评价用户的用户名
	 */
	private String commentUserName;

	/**
	 * 被评价用户的用户id
	 */
	private Long toCommentUserId;

	/**
	 * 被评价用户的用户名
	 */
	private String toCommentUserName;

	/**
	 * 评价内容
	 */
	private String content;

	/**
	 * 是否已被屏蔽的标记
	 */
	private Integer stateFlag;

	public CommentVO() {
	}

	public CommentVO(Long id,Integer level, String commentTime, Long commentUserId,
			String commentUserName, Long toCommentUserId,
			String toCommentUserName, String content) {
		this.id = id;
		this.level = level;
		this.commentTime = commentTime;
		this.commentUserId = commentUserId;
		this.commentUserName = commentUserName;
		this.toCommentUserId = toCommentUserId;
		this.toCommentUserName = toCommentUserName;
		this.content = content;
	}

	public CommentVO(Integer level, String commentTime, Long commentUserId,
			String commentUserName, Long toCommentUserId,
			String toCommentUserName, String content, Integer stateFlag) {
		this.level = level;
		this.commentTime = commentTime;
		this.commentUserId = commentUserId;
		this.commentUserName = commentUserName;
		this.toCommentUserId = toCommentUserId;
		this.toCommentUserName = toCommentUserName;
		this.content = content;
		this.stateFlag = stateFlag;
	}

	public CommentVO(Long id, Integer level, String commentTime,
			Long commentUserId, String commentUserName, Long toCommentUserId,
			String toCommentUserName, String content, Integer stateFlag) {
		this.id = id;
		this.level = level;
		this.commentTime = commentTime;
		this.commentUserId = commentUserId;
		this.commentUserName = commentUserName;
		this.toCommentUserId = toCommentUserId;
		this.toCommentUserName = toCommentUserName;
		this.content = content;
		this.stateFlag = stateFlag;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(String commentTime) {
		this.commentTime = commentTime;
	}

	public Long getCommentUserId() {
		return commentUserId;
	}

	public void setCommentUserId(Long commentUserId) {
		this.commentUserId = commentUserId;
	}

	public Long getToCommentUserId() {
		return toCommentUserId;
	}

	public void setToCommentUserId(Long toCommentUserId) {
		this.toCommentUserId = toCommentUserId;
	}

	public String getCommentUserName() {
		return commentUserName;
	}

	public void setCommentUserName(String commentUserName) {
		this.commentUserName = commentUserName;
	}

	public String getToCommentUserName() {
		return toCommentUserName;
	}

	public void setToCommentUserName(String toCommentUserName) {
		this.toCommentUserName = toCommentUserName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getStateFlag() {
		return stateFlag;
	}

	public void setStateFlag(Integer stateFlag) {
		this.stateFlag = stateFlag;
	}

}
