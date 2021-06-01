package com.megumi.repository;

import com.megumi.pojo.PTag;
import com.megumi.pojo.Picture;
import com.megumi.pojo.middle.TagPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Collection;

public interface ImgTagRepo extends JpaRepository<TagPicture, Long> {

    void deleteBypTagInAndPicture(Collection<PTag> pTags, Picture picture);


}
