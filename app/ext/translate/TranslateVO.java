package ext.translate;

import java.util.ArrayList;
import java.util.List;

public class TranslateVO {
	
	public String errorCode = "";
	
	public String errorMsg = "";
	
	public String from = "auto";
	
	public String to = "auto";
	
	public List<SrcDst> results = new ArrayList<SrcDst>();
	
}
