import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.File;

public class MicroblogJ {
	
	static final String[] iconName = {"pencil", "link", "quote-left", "film", "picture-o", "music"}; 

	public static void main(String[] args) throws FileNotFoundException {
		Date date = new Date();
		String postDateTime = new java.text.SimpleDateFormat("yyyy年M月dd日 E HH:mm").format(date);
		String timeStamp = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(date);
		String timeTag = String.format("<time datetime=\"%s\"><i class=\"fa fa-clock-o\" aria-hidden=\"true\"></i> %s</time>", timeStamp, postDateTime);
		
		Scanner postIn = new Scanner(new File("newPost"));
		String postType = postIn.next().toLowerCase();
		long postId = date.getTime();
		String typeIcon = String.format("<a id=\"%d\" href=\"#%d\"><span class=\"fa-stack fa-2x\"><i class=\"fa fa-circle fa-stack-2x\"></i><i class=\"fa fa-%s fa-stack-1x fa-inverse\"></i></span></a>", postId, postId, iconName[getTypeId(postType)]);
		
		String postHead = "";
		switch(postType){
		case "text":
		case "video":
		case "music":
			postHead = String.format("<h4>%s</h4>", postIn.nextLine().trim());
			break;
		case "link":
			try{
				String link = postIn.next();
				String linkTitle = postIn.nextLine().trim();
				postHead = String.format("<h4><a target=\"_blank\" href=\"%s\">%s</a></h4>", link, linkTitle);
			} catch(Exception e){
				System.err.println("Error: wrong file format.");
			}
			break;
		case "quote":
			postHead = String.format("<h4><blockquote>%s</blockquote></h4>", postIn.nextLine());
			break;
		
		case "picture":
			String img = postIn.nextLine().trim();

			if(img.startsWith("http")){
				postHead = String.format("<h4><img src=\"%s\" class=\"img-responsive\"></h4>", img);
			} else{
				postHead = String.format("<h4>%s</h4>", img);
			}
			break;
			
			default:
				System.err.println("Error: unsupported post type!");
				break;
		}
		
		StringBuilder postContent = new StringBuilder("<p>");
		String s = postIn.nextLine();
		while(s.equals("-----") == false){
			postContent.append(s + "<br>\n");
			s = postIn.nextLine();
		}
		postContent.append("</p>");
		
		String tags = "";
		if(postIn.hasNext()){
			tags = String.format("<div class=\"tag\"><i class=\"fa fa-tags\" aria-hidden=\"true\"></i> %s</div>", postIn.nextLine());
		}
		
		postIn.close();
		String post = String.format("<div class=\"col-lg-6\">\n<div class=\"row\">\n<div class=\"col-md-1 type\">%s</div>\n<div class=\"col-md-11\">\n%s\n%s\n%s\n%s\n</div>\n</div>\n</div>\n<hr>\n", typeIcon, timeTag, postHead, postContent, tags);
		//System.out.println(post);
		
		Scanner blogReader = new Scanner(new File("public/index.html"));
		StringBuilder newBlogContent = new StringBuilder();
		
		while(blogReader.hasNext()){
			String line = blogReader.nextLine();
			if(line.endsWith("<!-- INSERT HERE -->")){
				newBlogContent.append("<!-- INSERT HERE -->\n" + post + '\n' );
			}else{
				newBlogContent.append(line + '\n');
			}
		}
		
		blogReader.close();
		
		PrintWriter blogUpdater = new PrintWriter(new File("public/index.html"));
		blogUpdater.print(newBlogContent);
		blogUpdater.close();
	}

	private static int getTypeId(String type){
		switch (type){
		case "text":
			return 0;
		case "link":
			return 1;
		case "quote":
			return 2;
		case "video":
			return 3;
		case "picture":
			return 4;
		case "music":
			return 5;
		}
		
		return -1;
	}
}
