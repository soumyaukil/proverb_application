/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proverbs.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import proverbs.models.Proverb;
import proverbs.models.ProverbViewModel;
import proverbs.models.Tag;

/**
 *
 * @author soumukil
 */
public class ProverbHelper {

    private SessionFactory sessionFactory;

    @Autowired
    public ProverbHelper(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<ProverbViewModel> getProverbs(int start, int end) {
        List<ProverbViewModel> list = new ArrayList<>();
        List<Proverb> proverbList = loadProverbs(start, end);
        for (Proverb proverb : proverbList) {
            list.add(ProverbViewModel
                    .builder()
                    .text(proverb.getDescription())
                    .id(proverb.getProverb_id())
                    .tags(loadTags(proverb.getProverb_id()))
                    .build());
        }
        return list;
    }

    private List<Proverb> loadProverbs(int start, int end) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            final Criteria criteria = session.createCriteria(Proverb.class);
            criteria.setFirstResult(start);
            criteria.setMaxResults(end);
            List<Proverb> list = criteria.list();
            //System.err.println("List " + list.size() + " " + start + " " + end);
            tx.commit();
            return list;
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return Collections.EMPTY_LIST;
    }

    private List<String> loadTags(final String proverb_id) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            final Criteria criteria = session.createCriteria(Tag.class);
            criteria.add(Restrictions.eq("proverb_id", proverb_id));
            List<Tag> list = criteria.list();
            tx.commit();
            return list.stream().map(item -> item.getName()).collect(Collectors.toList());
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return Collections.EMPTY_LIST;
    }
}
