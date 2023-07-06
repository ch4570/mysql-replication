package replication.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import replication.test.domain.Member;
import replication.test.repository.MemberRepository;
import replication.test.service.MemberService;

@SpringBootTest
public class ReplicationTest {

    private final MemberService memberService;

    @Autowired
    public ReplicationTest(MemberService memberService) {
        this.memberService = memberService;
    }

    @Test
    void replicationTest() {
        int count = 12;

        for (int i=1; i<=12; i++) {
            Member member = Member.builder()
                    .address("서울" + i)
                    .name("코린이" + i)
                    .build();

            memberService.saveMember(member);
        }

        for (int i=1; i<=12; i++) {
            memberService.findAll();
        }


    }
}
