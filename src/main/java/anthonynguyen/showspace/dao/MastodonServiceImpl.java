package anthonynguyen.showspace.dao;

import com.sys1yagi.mastodon4j.MastodonClient;
import com.sys1yagi.mastodon4j.MastodonRequest;
import com.sys1yagi.mastodon4j.api.Pageable;
import com.sys1yagi.mastodon4j.api.entity.Status;
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException;
import com.sys1yagi.mastodon4j.api.method.Statuses;
import com.sys1yagi.mastodon4j.api.method.Timelines;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MastodonServiceImpl implements MastodonService {
    private final MastodonClient mastodonClient;
    private final Timelines timelines;
    private final Statuses statuses;

    @Autowired
    public MastodonServiceImpl(MastodonClient mastodonClient) {
        this.mastodonClient = mastodonClient;
        this.timelines = new Timelines(mastodonClient);
        this.statuses = new Statuses(mastodonClient);
    }

    @Override
    public List<Status> getHomeFeed() {
        try {
            // Get the request object
            MastodonRequest<Pageable<Status>> request = timelines.getHome();
            // Execute the request to get the statuses
            Pageable<Status> pageableStatus = request.execute();
            // Get the statuses from the pageable object into a list
            List<Status> statusList = pageableStatus.getPart();
            // Filter out unwanted content and limit to top 3 posts
            return statusList.stream()
                    .filter(status -> status.getTags().stream().anyMatch(tag -> tag.getName().equalsIgnoreCase("showspace")))
                    .limit(3) // Limit to top 3 posts
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log error and handle appropriately
            return Collections.emptyList();
        }
    }

    @Override
    public void createPost(String content) {
        try {
        	
            Status status = statuses.postStatus(content, null, null, false, null).execute();

        } catch (Mastodon4jRequestException e) {
            // Log the error and handle appropriately
        }
    }
}