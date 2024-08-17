package com.nuzzle.backend.picture.repository;

import com.nuzzle.backend.picture.domain.Picture;
import com.nuzzle.backend.picture.domain.mapping.PictureEmoji;
import com.nuzzle.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PictureEmojiRepository extends JpaRepository<PictureEmoji, Long> {
    List<PictureEmoji> findByPicture(Picture picture);  // Picture 객체를 기반으로 이모티콘 정보 조회

}
