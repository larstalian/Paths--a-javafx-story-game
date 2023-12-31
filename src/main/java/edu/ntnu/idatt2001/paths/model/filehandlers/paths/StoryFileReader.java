package edu.ntnu.idatt2001.paths.model.filehandlers.paths;

import edu.ntnu.idatt2001.paths.model.actions.Action;
import edu.ntnu.idatt2001.paths.model.filehandlers.factories.ActionFactory;
import edu.ntnu.idatt2001.paths.model.story.Link;
import edu.ntnu.idatt2001.paths.model.story.Passage;
import edu.ntnu.idatt2001.paths.model.story.Story;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

/**
 * The StoryFileReader class provides utility methods for reading stories from text files and
 * creating corresponding Story objects. This class is designed to be used in a static context and
 * should not be instantiated.
 *
 * <p>The class offers a method to read a story from a text file and create a Story object that
 * represents the story. The method {@link #readStoryFromFile(String)} takes a filename and reads
 * the content of the file, then parses the content to create a Story object. If any I/O error
 * occurs while reading the story, an IOException is thrown.
 *
 * <p>The StoryFileReader class utilizes the {@link Story}, {@link Passage}, {@link Link}, and
 * {@link Action} classes as components to build the content of the story from the text file.
 *
 * <p>This class complements the {@link StoryFileWriter} class, which provides methods for saving
 * stories to text files.
 *
 * @see StoryFileWriter
 */
public class StoryFileReader {

  private static final String DELIMITER = System.getProperty("line.separator");
  private static final String FILE_ENDING = ".paths";
  private static final Path FILE_PATH = Paths.get("src/main/resources/stories/paths/");
  private static final String PASSAGE_PATTERN = "^::(.+)$";
  private static final String LINK_PATTERN = "^\\[(.+)]\\((.+)\\)$";
  private static final String ACTIONS_PATTERN = "^\\{(.+)}$";
  private static final String ACTION_TYPE_PATTERN = "^([HISG]):";

  private StoryFileReader() {
  }

  /**
   * Reads the story from the specified file and returns a Story object.
   *
   * @param storyTitle The name of the file containing the story.
   * @return A Story object representing the story read from the file.
   * @throws IOException    If there is a problem reading the file.
   * @throws ParseException If there is a problem parsing the story content.
   */
  public static Story readStoryFromFile(String storyTitle) throws IOException, ParseException {
    storyTitle = FilenameUtils.removeExtension(storyTitle);
    String storyContent = readFile(storyTitle);
    return parseStoryContent(storyContent);
  }

  /**
   * Parses the story content and creates a corresponding Story object.
   *
   * @param storyContent The content of the story as a string.
   * @return A Story object representing the story.
   */
  private static Story parseStoryContent(String storyContent) throws ParseException {
    String[] lines = storyContent.split(DELIMITER);

    if (lines.length < 4) {
      throw new ParseException("Invalid story format: Not enough lines in the input file", 0);
    }

    String storyTitle = lines[0];
    Passage openingPassage = createPassage(lines, 2);
    Story story = new Story(storyTitle, openingPassage);

    for (int i = 4; i < lines.length; i++) {
      if (isPassage(lines[i])) {
        Passage passage = createPassage(lines, i);
        story.addPassage(passage);
      }
    }
    return story;
  }

  /**
   * Reads the content of the specified file as a string.
   *
   * @param fileName The name of the file to read.
   * @return The content of the file as a string.
   * @throws IOException If there is a problem reading the file.
   */
  private static String readFile(String fileName) throws IOException {
    Path filePath = FILE_PATH.resolve(fileName + FILE_ENDING);
    return Files.readString(filePath);
  }

  /**
   * Checks if the provided line represents a passage.
   *
   * @param line The line to check.
   * @return true if the line represents a passage, false otherwise.
   */
  private static boolean isPassage(String line) {
    return line.matches(PASSAGE_PATTERN);
  }

  /**
   * Checks if the provided line represents a link.
   *
   * @param line The line to check.
   * @return true if the line represents a link, false otherwise.
   */
  private static boolean isLink(String line) {
    return line.matches(LINK_PATTERN);
  }

  /**
   * Checks if the provided line represents a set of actions.
   *
   * @param line The line to check.
   * @return true if the line represents a set of actions, false otherwise.
   */
  private static boolean isActions(String line) {
    return line.matches(ACTIONS_PATTERN);
  }

  /**
   * Creates a Passage object from the provided lines starting at the specified index.
   *
   * @param lines An array of strings representing the story content.
   * @param index The index in the array where the passage information starts.
   * @return A Passage object representing the passage.
   */
  private static Passage createPassage(String[] lines, int index) throws ParseException {
    Matcher passageMatcher = Pattern.compile(PASSAGE_PATTERN).matcher(lines[index]);
    if (passageMatcher.find()) {
      String passageTitle = passageMatcher.group(1);
      String passageContent = lines[index + 1];
      Passage passage = new Passage(passageTitle, passageContent);
      addLinks(passage, index + 2, lines);
      return passage;
    }
    throw new ParseException("Invalid passage format: " + lines[index], index);
  }

  /**
   * Adds links to the specified passage based on the provided lines starting at the specified
   * index.
   *
   * @param passage The passage to add links to.
   * @param index   The index in the array where the link information starts.
   * @param lines   An array of strings representing the story content.
   */
  private static void addLinks(Passage passage, int index, String[] lines) {
    ListIterator<String> lineIterator = Arrays.asList(lines).listIterator(index);

    while (lineIterator.hasNext()) {
      String line = lineIterator.next();
      if (isPassage(line) || line.isEmpty() || isActions(line)) {
        break;
      } else if (isLink(line)) {
        Matcher linkMatcher = Pattern.compile(LINK_PATTERN).matcher(line);
        if (linkMatcher.find()) {
          String linkText = linkMatcher.group(1);
          String linkRef = linkMatcher.group(2);
          Link link = new Link(linkText, linkRef);
          if (lineIterator.hasNext()) {
            String nextLine = lineIterator.next();
            if (isActions(nextLine)) {
              buildActionsContent(link, nextLine);
            } else {
              // If the line is not an action, go back one step in the iterator
              lineIterator.previous();
            }
          }
          passage.addLink(link);
        } else {
          throw new IllegalArgumentException("Invalid link format: " + line);
        }
      }
    }
  }

  /**
   * Builds actions for the provided link based on the specified line containing action
   * information.
   *
   * @param link The link to add actions to.
   * @param line The line containing action information.
   */
  private static void buildActionsContent(Link link, String line) {
    Matcher actionsMatcher = Pattern.compile(ACTIONS_PATTERN).matcher(line);
    if (actionsMatcher.find()) {
      String actions = actionsMatcher.group(1);
      String[] actionStrings = actions.split(",");

      for (String action : actionStrings) {
        Matcher actionTypeMatcher = Pattern.compile(ACTION_TYPE_PATTERN).matcher(action);
        if (actionTypeMatcher.find()) {
          String actionType = actionTypeMatcher.group(1);
          String actionValue = action.substring(2);

          Action actionObject = ActionFactory.createActionFromPathFormat(actionType, actionValue);
          link.addAction(actionObject);
        }
      }
    }
  }

  /**
   * Returns a collection of the names of the stories saved in the stories folder.
   *
   * @return A collection of the names of the stories saved in the stories folder.
   */
  public static Collection<String> getSavedStories() {
    File folder = new File(FILE_PATH.toString());
    String[] savedStories = folder.list();
    return savedStories == null ? Collections.emptyList() : Arrays.asList(savedStories);
  }

  /**
   * Returns the file ending used for story files.
   *
   * @return The file ending used for story files.
   */
  public static String getFileEnding() {
    return FILE_ENDING;
  }
}
