package Presentation;
import Business.Link;
import Business.Wiki;

public class TestSerialization {
	public static void main(String[] args) {
		Wiki wiki = new Wiki();
		String filename = "Wiki_File";
		wiki.addLink("https://ru.wikipedia.org/wiki/Java");
		wiki.addLink("https://ru.wikipedia.org/wiki/CORBA");
		wiki.addLink("https://ru.wikipedia.org/wiki/Perl");	
		//wiki.save(filename);
		//wiki.read(filename);
		for(Link link: wiki.getLinks())
			System.out.println(link);
	}
}
