package ariefsyaifu.service;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import ariefsyaifu.client.TagClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TagService {

    @Inject
    @RestClient
    TagClient tagClient;

    public List<String> getTagIds(String userId) {
        return tagClient.getTags(userId);
    }

}
