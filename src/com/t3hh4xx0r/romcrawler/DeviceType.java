package com.t3hh4xx0r.romcrawler;

public enum DeviceType {
	   TORO("http://rootzwiki.com/forum/362-cdma-galaxy-nexus-developer-forum"), 
	   VIGOR("http://rootzwiki.com/forum/267-rezound-developer-forum/"),
	   ACE("http://rootzwiki.com/forum/163-desire-hd-developer-forum"),
	   ERIS("http://rootzwiki.com/forum/34-droid-eris-developer-forum"),
	   INC("http://rootzwiki.com/forum/22-droid-incredible-developer-forum"),
	   VIVOW("http://rootzwiki.com/forum/60-droid-incredible-2-developer-forum"),
	   SHOOTER("http://rootzwiki.com/forum/113-evo-3d-developer-forum"),
	   SUPERSONIC("http://rootzwiki.com/forum/36-evo-4g-developer-forum"),
	   SPEEDY("http://rootzwiki.com/forum/202-evo-shift-4g-developer-forum"),
	   VISION("http://rootzwiki.com/forum/109-g2-vision-developer-forum"),
	   DOUBLESHOT("http://rootzwiki.com/forum/142-mytouch-4g-slide-developer-forum"),
	   PASSION("http://rootzwiki.com/forum/75-nexus-one-developer-forum"),
	   PYRAMID("http://rootzwiki.com/forum/88-sensation-4g-development-forum"),
	   BIONIC("http://rootzwiki.com/forum/82-droid-bionic-developer-forum"),
	   OLYMPUS("http://rootzwiki.com/forum/44-atrix-developer-forum"),
	   CDMA_SPYDER("http://rootzwiki.com/forum/338-droid-razr-developer-forum"),
	   MECHA("http://rootzwiki.com/forum/12-thunderbolt-developer-forum"),
	   MAGURO("http://rootzwiki.com/forum/230-gsm-galaxy-nexus-developer-forum"),
	   SHOLES("http://http://rootzwiki.com/forum/28-droid-1-developer-forum/"),
	   CDMA_DROID2("http://rootzwiki.com/forum/15-droid-2r2d2-developer-forum"),
	   CDMA_DROID2WE("http://rootzwiki.com/forum/76-droid-2-global-developer-forum"),
	   CRESPO("http://http://rootzwiki.com/forum/32-nexus-s-developer-forum/"),
	   CRESPO4G("http://rootzwiki.com/forum/140-nexus-s-4g-developer-forum/"),
	   GALAXYS2("http://rootzwiki.com/forum/96-galaxy-s-ii-developer-forum/"),
	   CDMA_SHADOW("http://rootzwiki.com/forum/16-droid-x-developer-forum");	   


	    private final String forumUrl;

	    private DeviceType(String forumUrl) {
	        this.forumUrl = forumUrl;
	    }

	    public String getForumUrl() {
	        return forumUrl;
	    }
}
