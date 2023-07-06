package replication.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import replication.test.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
