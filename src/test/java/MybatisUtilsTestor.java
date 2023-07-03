import com.imooc.oa.utils.MybatisUtils;
import org.junit.Test;

public class MybatisUtilsTestor {
    @Test
    public void testCase1(){
        MybatisUtils.executeQuery(sqlSession -> {
            String out = (String) sqlSession.selectOne("test.sample");
            return out;
        });
    }

    @Test
    public void testCase2(){
        String result = (String) MybatisUtils.executeQuery(sqlSession -> sqlSession.selectOne("test.sample"));
        System.out.println(result);
    }

}
