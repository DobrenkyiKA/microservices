Feature: Resource Management
  As a music streaming application user
  I want to manage audio file resources
  So that I can upload, retrieve, and delete music files

  Background:
    Given the resource service is running
    And the system is ready to process requests

  Scenario: Successfully upload a new audio resource
    Given I have a valid audio file "song.mp3" with size 5242880 bytes
    When I upload the audio resource
    Then the resource should be successfully stored
    And I should receive a resource ID in response
    And a resource created event should be published to the message queue
    And the resource should be accessible for download

  Scenario: Retrieve an existing audio resource
    Given I have previously uploaded an audio resource with ID 1
    When I request to download the resource with ID 1
    Then I should receive the audio file data
    And the response should contain the correct resource ID
    And the audio data should match the originally uploaded file

  Scenario: Delete single audio resource
    Given I have uploaded audio resources with IDs "1,2,3"
    When I request to delete the resource with ID "2"
    Then the resource with ID 2 should be removed from storage
    And I should receive confirmation of deletion for ID 2
    And the song metadata should be cleaned up in the song service
    And other resources with IDs 1 and 3 should remain intact

  Scenario: Delete multiple audio resources
    Given I have uploaded audio resources with IDs "10,11,12,13,14"
    When I request to delete resources with IDs "11,13,14"
    Then the resources with IDs 11, 13, and 14 should be removed from storage
    And I should receive confirmation of deletion for IDs "11,13,14"
    And the song metadata should be cleaned up for all deleted resources
    And resources with IDs 10 and 12 should remain intact

  Scenario: Handle deletion of non-existing resources
    Given there are no resources with IDs "999,1000"
    When I request to delete resources with IDs "999,1000"
    Then I should receive an empty deletion confirmation
    And no resources should be affected in the system
    And the system should handle the request gracefully

  Scenario: Process resource creation workflow end-to-end
    Given I have a valid audio file "complete-workflow-test.mp3"
    When I upload the audio resource through the complete workflow
    Then the resource should be stored in AWS S3 storage
    And resource metadata should be saved in the database
    And a resource created event should be published via Kafka
    And the resource processor should receive the event for metadata extraction
    And I should be able to retrieve the resource using its ID

  Scenario Outline: Upload various audio file formats
    Given I have a valid audio file "<filename>" with format "<format>"
    When I upload the audio resource
    Then the resource should be successfully processed
    And the system should handle "<format>" format correctly

    Examples:
      | filename        | format |
      | test-song.mp3   | MP3    |
      | classical.wav   | WAV    |
      | jazz-track.flac | FLAC   |
      | podcast.m4a     | M4A    |

  Scenario: Handle system error during resource upload
    Given the AWS S3 service is temporarily unavailable
    When I attempt to upload an audio resource
    Then I should receive an appropriate error response
    And the system should not create incomplete resource records
    And no resource created events should be published
    And the system should maintain data consistency