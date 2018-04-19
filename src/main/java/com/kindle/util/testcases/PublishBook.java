package com.kindle.util.testcases;

import java.io.File;

import com.kindle.util.D2DHome;

public class PublishBook {
	
	public static void main(String[] args) {
		D2DHome home=new D2DHome();
		int numOfBooks = home.getNumOfBooks();
		String[] titles = home.getTitles();
		String[] authors = home.getAuthors();
		String[] publishers = home.getPublishers();
		String[] searchTerms = home.getSearchTerms();
		String[] desc = home.getDesc();
		String fldrPath = home.getBooksFldrPath();
		String imgFldrPath = home.getImgFldrPath();
		home.login();
		for(int i=1; i <= numOfBooks; i++){
			System.out.println("Uploading book : "+i);
			String filePath = new File(fldrPath+i+".doc").getAbsolutePath();
			String imgPath = new File(imgFldrPath+i+".jpg").getAbsolutePath();
			int j = i-1;
			try{
				home.addNewBook();
				home.fillBookDetails(filePath, titles[j], authors[j], desc[j], publishers[j], searchTerms[j]);
				home.addSubjects();
				home.saveAndContinue();
				home.uploadCoverPage(imgPath);
				home.saveAndContinue();
				home.tickReviewBox();
				home.saveAndContinue(true);
				home.enterPrice("2.99");
				home.publishToAllStores();
				home.clickSubmit();
				home.confirmRights();
				home.publishMyBook();
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Uploading failed for book : "+i);
			}
		}
		home.logout();
	}
}
