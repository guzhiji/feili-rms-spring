package com.feiliks.testapp2;

import com.feiliks.testapp2.jpa.entities.Tag;
import com.feiliks.testapp2.jpa.repositories.TagRepository;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
