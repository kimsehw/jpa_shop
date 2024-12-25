package jpabook.jpashop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 회원가입() {
        Member member = new Member();
        member.setName("kim");

        Long saveId = memberService.join(member);

        assertThat(memberRepository.find(saveId)).isEqualTo(member);
    }

    @Test
    public void 중복_회원_예외() {

        Member member = new Member();
        member.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        Long id1 = memberService.join(member);

        assertThatThrownBy(() -> memberService.join(member2)).isInstanceOf(IllegalStateException.class);
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외2() throws Exception {

        Member member = new Member();
        member.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        Long id1 = memberService.join(member);
        Long id2 = memberService.join(member2);

        fail("예외가 발생해야 합니다.");
    }

}