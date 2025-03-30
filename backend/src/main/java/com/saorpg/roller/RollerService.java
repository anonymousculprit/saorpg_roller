package com.saorpg.roller;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saorpg.roller.dao.RollRepository;
import com.saorpg.roller.domain.DiceResult;
import com.saorpg.roller.domain.Roll;
import com.saorpg.roller.domain.RollMetadata;

import jakarta.transaction.Transactional;

@Service
public class RollerService {
  private final ObjectProvider<List<DiceResult>> diceRoller;
  private final RollRepository rollRepository;

  @Autowired
  public RollerService(ObjectProvider<List<DiceResult>> diceRoller, RollRepository rollRepository) {
    this.diceRoller = diceRoller;
    this.rollRepository = rollRepository;
  }

  public Roll roll(RollMetadata metadata) {
    return this.rollNTimes(1, metadata).get(0);
  }

  @Transactional
  public List<Roll> rollNTimes(int numberOfRolls, RollMetadata metadata) {
    if (numberOfRolls < 1) {
      throw new IllegalArgumentException("Cannot roll a non-positive number of times");
    }
    List<DiceResult> diceResults = this.diceRoller.getObject(numberOfRolls);
    List<Roll> rolls = diceResults.stream().map(result -> new Roll(metadata, result)).toList();
    return rollRepository.saveAll(rolls);
  }

  public Roll getRollById(int id) {
    return rollRepository.findById(id).orElseThrow();
  }

  public List<Roll> getRollsByCharacter(String character) {
    return rollRepository.findByMetadataCharacter(character);
  }

  public List<Roll> getRollsByPost(String post) {
    return rollRepository.findByMetadataPost(post);
  }
}
