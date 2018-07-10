package com.feiliks.rms;

import com.feiliks.rms.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BackgroundTasks {

    @Autowired
    private TagRepository tagRepo;

    /*
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void checkOrphanedTags() {
        Collection<Tag> tags = tagRepo.findOrphaned();
        int n = tags.size();
        if (n > 0) {
            System.out.print("delete orphaned tags:");
            System.out.println(n);
            tagRepo.delete(tags);
        }
    }
    */
}
