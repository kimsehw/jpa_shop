package jpabook.jpashop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    void 상품주문() {
        Member member = getMember("kim", new Address("경기도 시흥시", "서울대학로", "123"));

        int price = 10000;
        int stockQuantity = 10;
        String bookName = "시골 JPA";
        Book book = getBook(bookName, price, stockQuantity);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        Order order = orderRepository.findOne(orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getTotalPrice()).isEqualTo(price * orderCount);
        assertThat(book.getStockQuantity()).isEqualTo(stockQuantity - orderCount);
    }

    @Test
    void 재고수량초과() {
        Member member = getMember("kim", new Address("경기도 시흥시", "서울대학로", "123"));

        int price = 10000;
        int stockQuantity = 10;
        String bookName = "시골 JPA";
        Book book = getBook(bookName, price, stockQuantity);

        int orderCount = 11;
        assertThatThrownBy(() -> orderService.order(member.getId(), book.getId(), orderCount))
                .isInstanceOf(NotEnoughStockException.class);
    }

    @Test
    void 주문취소() {
        Member member = getMember("kim", new Address("경기도 시흥시", "서울대학로", "123"));

        int price = 10000;
        int stockQuantity = 10;
        String bookName = "시골 JPA";
        Book book = getBook(bookName, price, stockQuantity);

        int orderCount = 10;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        Order order = orderRepository.findOne(orderId);
        assertThat(book.getStockQuantity()).isEqualTo(stockQuantity - orderCount);
        order.cancel();
        assertThat(book.getStockQuantity()).isEqualTo(stockQuantity);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    private Member getMember(String name, Address address) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(address);
        em.persist(member);
        return member;
    }

    private Book getBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }


}