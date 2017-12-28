/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proverbs.crawler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import proverbs.models.Proverb;
import proverbs.models.Tag;
import proverbs.models.URL;
import proverbs.utils.Constants;
import static proverbs.utils.Constants.CRAWL_URL;
import static proverbs.utils.Constants.HIPERLINK_TAG;
import static proverbs.utils.Constants.HREF_TAG;
import static proverbs.utils.Constants.HREF_TAG_LOOKUP_STRING;
import static proverbs.utils.Constants.ORIGIN_LANGUAGE;
import static proverbs.utils.Constants.PROVERB_BODY_LOOKUP_STRING;
import static proverbs.utils.Constants.PROVERB_LOOKUP_STRING;

/**
 *
 * @author soumukil
 */
public class WebCrawler {

    private final SessionFactory sessionFactory;

    Map<String, Boolean> pageUrls = new HashMap<>();
    Map<String, Boolean> proverbUrls = new HashMap<>();

    @Autowired
    public WebCrawler(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void crawl() {
        loadProverbsFromDb();
        crawlHelper(CRAWL_URL, CRAWL_URL);
    }

    private void crawlHelper(String url, String baseUrl) {
        if (pageUrls.containsKey(url.trim())) {
            System.out.println("Discarded URL: " + url.trim());
            return;
        }
        pageUrls.put(url.trim(), Boolean.TRUE);
        System.out.println("URL: " + url);

        Document doc = getDocument(url);
        Elements proverbBody = doc.select(PROVERB_BODY_LOOKUP_STRING);

        for (Element proverb : proverbBody) {
            List<Element> proverbDiv = proverb.getElementsByClass(PROVERB_LOOKUP_STRING);
            if (proverbDiv.size() == 1 && !proverbUrls.containsKey(proverbDiv.get(0).attr("href").trim())) {
                Proverb proverbElem = Proverb
                        .builder()
                        .description(proverbDiv.get(0).ownText())
                        .origin_language(ORIGIN_LANGUAGE)
                        .proverb_id(UUID.randomUUID().toString())
                        .build();
                saveProverbIntoDb(proverbElem);

                URL urlElem = URL
                        .builder()
                        .id(proverbDiv.get(0).attr("href").trim())
                        .build();
                saveProverbIntoDb(urlElem);
                proverbUrls.put(proverbDiv.get(0).attr("href").trim(), Boolean.TRUE);

                List<Element> tags = proverb.getElementsByClass(Constants.TAG_LOOKUP_STRING);
                for (Element tag : tags) {
                    Tag tagElem = Tag
                            .builder()
                            .id(UUID.randomUUID().toString())
                            .proverb_id(proverbElem.getProverb_id())
                            .name(tag.ownText())
                            .build();
                    saveProverbIntoDb(tagElem);
                }
            }
        }
        Elements links = doc.select(HIPERLINK_TAG);
        for (Element link : links) {
            if (link.attr(HREF_TAG).startsWith(baseUrl)) {
                crawlHelper(link.attr(HREF_TAG), baseUrl);
            }
        }
    }

    private Document getDocument(final String url) {
        Document doc = null;
        while (true) {
            try {
                doc = Jsoup.connect(url).timeout(0).userAgent(Constants.USER_AGENT).get();
                break;
            } catch (ConnectException ex) {
                System.err.println("ConnectException occured: " + url);
            } catch (SocketTimeoutException ex) {
                System.err.println("SocketTimeoutException occured: " + url);
            } catch (IOException ex) {
                System.err.println("IOException occured: " + url);
            }
        }
        return doc;
    }

    private void saveProverbIntoDb(Object proverb) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.save(proverb);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    private void loadProverbsFromDb() {
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            final Criteria criteria = session.createCriteria(URL.class);
            List<URL> list = criteria.list();
            list.stream().forEach(item -> proverbUrls.put(item.getId(), Boolean.TRUE));
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
