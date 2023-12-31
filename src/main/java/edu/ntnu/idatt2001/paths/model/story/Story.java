package edu.ntnu.idatt2001.paths.model.story;

import static edu.ntnu.idatt2001.paths.model.story.Story.StoryConstants.MAX_TITLE_LENGTH;
import static edu.ntnu.idatt2001.paths.model.story.Story.StoryConstants.MIN_TITLE_LENGTH;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;

/**
 * The Story class represents a story, or branching composed of interconnected passages.
 *
 * <p>Each passage is represented by a Passage object, and can be linked to other passages using
 * Link objects. The opening passage of the story is represented by a Passage object passed to the
 * constructor of the Story object. Passages can be added to the story using the
 * {@link #addPassage(Passage)} method, and the full list of passages can be accessed using the
 * {@link #getPassages()} method. Passages can be retrieved using the {@link #getPassage(Link)}
 * method.
 *
 * <p>The Story class is immutable, and its properties cannot be modified once the object is
 * constructed. The properties of the story include its title, represented by a String object, and
 * its passages, represented by a Map of Link objects and Passage objects.
 *
 * <p>The Story field variables constraints are defined in the {@link StoryConstants} inner class
 *
 * @see Passage
 * @see Link
 * @see StoryConstants
 */
@EqualsAndHashCode(of = {"title", "openingPassage"})
public class Story {

  @JsonProperty
  private final String title;
  @JsonProperty
  private final Passage openingPassage;
  @JsonProperty
  private final Map<Link, Passage> passages;

  /**
   * Constructs a new Story object with the given title and opening passage.
   *
   * @param title          the title of the story
   * @param openingPassage the opening passage of the story
   */
  @JsonCreator
  public Story(
      @JsonProperty("title") String title, @JsonProperty("openingPassage") Passage openingPassage) {

    if (title.length() < MIN_TITLE_LENGTH || title.length() > MAX_TITLE_LENGTH) {
      throw new IllegalArgumentException(
          "Title must be between " + MIN_TITLE_LENGTH + " and " + MAX_TITLE_LENGTH + " characters");
    }
    this.title = title;
    this.openingPassage = Objects.requireNonNull(openingPassage, "Opening passage cannot be null");
    this.passages = new LinkedHashMap<>();
  }

  /**
   * Returns the title of the story.
   *
   * @return the title of the story
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the opening passage of the story.
   *
   * @return the opening passage of the story
   */
  public Passage getOpeningPassage() {
    return openingPassage;
  }

  /**
   * Adds a passage to the story.
   *
   * @param passage the passage to be added
   * @return {@code true} if the passage was added to the story, {@code false} if the passage
   * already exists
   */
  public boolean addPassage(final Passage passage) {
    Objects.requireNonNull(passage, "Passage cannot be null");
    Link link = new Link(passage.getTitle(), passage.getTitle());
    if (passages.containsKey(link)) {
      return false;
    }
    passages.put(link, passage);
    return true;
  }

  /**
   * Returns the passage with the given link.
   *
   * @param link the link to the passage
   * @return the passage with the given link
   * @throws NoSuchPassageException if the link is {@code null}
   */
  public Passage getPassage(Link link) {
    if (passages.get(link) == null) {
      throw new NoSuchPassageException();
    }
    return (passages.get(link));
  }

  /**
   * Returns a collection of all the passages in the story.
   *
   * @return a collection of all the passages in the story
   */
  public Collection<Passage> getPassages() {
    return new LinkedHashSet<>(passages.values());
  }

  /**
   * Removes the passage associated with the specified link from this game's passages map. A passage
   * is removed only if it is not referenced by any other passage.
   *
   * @param link the link whose associated passage is to be removed from the map
   * @return {@code true} if the map changed as a result of the operation, {@code false} otherwise
   */
  public boolean removePassage(Link link) {
    if (passages.get(link) == null) {
      throw new NoSuchPassageException();
    }

    if (passages.values().stream().noneMatch(passage -> passage.getLinks().contains(link))) {
      return passages.remove(link) != null;
    }
    return false;
  }

  /**
   * Returns a string representation of the story, including its title, opening passage, and all
   * passages in the story.
   *
   * @return a string representation of the story
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Title: ").append(title).append("\n");
    sb.append("Opening Passage:\n");
    sb.append(openingPassage.getContent()).append("\n");
    sb.append("Passages:\n");
    List<Passage> passageList = new ArrayList<>(passages.values());
    passageList.sort(Comparator.comparing(Passage::getTitle));
    for (Passage passage : passageList) {
      sb.append("- ")
          .append(passage.getTitle())
          .append(": ")
          .append(passage.getContent())
          .append("\n");
    }
    return sb.toString();
  }

  /**
   * Returns a collection of all the broken links in the story, that is, links that reference a
   * passage that does no longer exist.
   *
   * @return a collection of all the broken links in the story
   */
  public Collection<Link> getBrokenLinks() {
    return passages.values().stream()
        .flatMap(passage -> passage.getLinks().stream())
        .filter(
            link -> !passages.containsKey(link) && !link.getRef().equals(openingPassage.getTitle()))
        .collect(Collectors.toSet());
  }

  /**
   * Removes all links to the specified passage in the story.
   *
   * <p>This method iterates through all the passages in the story and removes any link whose
   * reference is equal to the given passage title. This helps to ensure that there are no broken
   * links in the story after the deletion of a passage.
   *
   * @param passageTitle the title of the passage to remove all links to
   */
  public void removeAllLinksToPassage(String passageTitle) {
    Objects.requireNonNull(passageTitle, "Passage title cannot be null");
    openingPassage.getLinks().removeIf(link -> link.getRef().equals(passageTitle));
    passages
        .values()
        .forEach(
            passage -> passage.getLinks().removeIf(link -> link.getRef().equals(passageTitle)));
  }

  /**
   * The StoryConstants class contains constants used by the Story class to set the valid range of
   * its fields. The constants are declared as static and final, and can therefore not be modified.
   *
   * <p>Use the constants to check that parameter values are within the valid range when creating
   * or
   * modifying Story objects.
   *
   * @see Story
   */
  static class StoryConstants {

    static final int MIN_TITLE_LENGTH = 2;
    static final int MAX_TITLE_LENGTH = 50;
  }
}
