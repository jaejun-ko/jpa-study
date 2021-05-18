package com.sample;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {
            logic(em, tx);
            // tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    public static void logic(EntityManager em, EntityTransaction tx) {

        tx.begin();
        String id = "id1";
        Member member = new Member();
        member.setId(id);
        member.setUsername("Ko");
        member.setAge(32);

        em.persist(member);

        tx.commit();
        tx.begin();

        Member findMember = em.find(Member.class, id);      /* 이 때는 flush() 가 호출되지 않는다. */
        System.out.println("[DEBUG] findMember=" + findMember.getUsername() + ", age=" + findMember.getAge());

        member.setUsername("Lee");
        member.setAge(12);

        TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m", Member.class);
        List<Member> members = query.getResultList();       /* JPQL 쿼리 실행 시 flush() 가 자동 호출된다. 그러므로 member.setAge(12) 가 데이터베이스에 반영 되게 된다. 하지만 Database Commit 까지 수행되진 않는다. */
        System.out.println("[DEBUG] members.get(0).username=" + members.get(0).getUsername() + ", members.get(0).age=" + members.get(0).getAge());
        System.out.println("[DEBUG] members.size=" + members.size());
        tx.commit();

        // em.remove(member);
    }
}
