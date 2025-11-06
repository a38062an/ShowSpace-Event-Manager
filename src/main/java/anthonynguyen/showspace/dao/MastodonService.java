package anthonynguyen.showspace.dao;

import com.sys1yagi.mastodon4j.api.entity.Status;

import java.util.List;

public interface MastodonService {

    // This function should return recent 3 posts
    List<Status> getHomeFeed();

    // This should let you share a post with a text input
    void createPost(String content);

}
