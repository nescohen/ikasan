/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.connector.sftp.consumer;

import org.ikasan.framework.factory.DirectoryURLFactory;

/**
 * SFTP Consumer Configuration model.
 * 
 * @author Ikasan Development Team
 */
public class SftpConsumerConfiguration
{
    /** Remote directory from which to discover files */
    protected String sourceDirectory;

    /** Regular expression for matching file names */
    protected String filenamePattern;

    /** Classname for source directories URLs factory */
    protected DirectoryURLFactory sourceDirectoryURLFactory;

    /** Whether we filterDuplicates what we are picking up - True by default */
    protected Boolean filterDuplicates = Boolean.TRUE;

    /** Filter on Filename - True by default */
    protected Boolean filterOnFilename = Boolean.TRUE;

    /** Filter on LastModifiedDate - True by default */
    protected Boolean filterOnLastModifiedDate = Boolean.TRUE;

    /** Rename the remote file once successfully retrieved */
    protected Boolean renameOnSuccess = Boolean.FALSE;

    /** Extension to use when renaming file */
    protected String renameOnSuccessExtension;

    /** Move the remote file to once successfully retrieved */
    protected Boolean moveOnSuccess = Boolean.FALSE;

    /** New path to use when moving the file */
    protected String moveOnSuccessNewPath;

    /** Sort result set by chronological order - false by default. */
    protected Boolean chronological = Boolean.FALSE;

    /** Chunk files when retrieving */
    protected Boolean chunking = Boolean.FALSE;

    /** Maximum size of chunk when chunking, defaults to 1MB */
    protected Integer chunkSize = Integer.valueOf(1048576);

    /** Attempt to verify integrity of retrieved file by comparing with a checksum supplied by the remote system */
    protected Boolean checksum = Boolean.FALSE;

    /** Minimum age (in seconds) of file to match */
    protected Long minAge = Long.valueOf(120);

    /** Whether or not we delete the file after picking it up */
    protected Boolean destructive = Boolean.FALSE;

    /** Maximum rows that housekeeping can deal with, defaults to -1 (ignore) */
    protected Integer maxRows = Integer.valueOf(-1);

    /** Number of days in age the files need to be to be considered for housekeeping, defaults to -1 (ignore) */
    protected Integer ageOfFiles = Integer.valueOf(-1);
    
    /** SFTP unqiue clientId */
    protected String clientID;

    /** SFTP cleanup journal on completion */
    protected Boolean cleanupJournalOnComplete = Boolean.TRUE;

    /** SFTP default Remote host */
    protected String remoteHost = String.valueOf("localhost");

    /** SFTP private key hosts */
    protected String privateKeyFilename;

    /** SFTP max retry attempts */
    protected Integer maxRetryAttempts = Integer.valueOf(3);

    /** SFTP default remote port */
    protected Integer remotePort = Integer.valueOf(22);

    /** SFTP known hosts */
    protected String knownHostsFilename;

    /** SFTP user */
    protected String username;

    /** SFTP password/passphrase */
    protected String password;

    /** SFTP remote port */
    protected Integer connectionTimeout = Integer.valueOf(60000);

    public String getSourceDirectory()
    {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory)
    {
        this.sourceDirectory = sourceDirectory;
    }

    public String getFilenamePattern()
    {
        return filenamePattern;
    }

    public void setFilenamePattern(String filenamePattern)
    {
        this.filenamePattern = filenamePattern;
    }

    public DirectoryURLFactory getSourceDirectoryURLFactory()
    {
        return sourceDirectoryURLFactory;
    }

    public void setSourceDirectoryURLFactory(DirectoryURLFactory sourceDirectoryURLFactory)
    {
        this.sourceDirectoryURLFactory = sourceDirectoryURLFactory;
    }

    public Boolean getFilterDuplicates()
    {
        return filterDuplicates;
    }

    public void setFilterDuplicates(Boolean filterDuplicates)
    {
        this.filterDuplicates = filterDuplicates;
    }

    public Boolean getFilterOnFilename()
    {
        return filterOnFilename;
    }

    public void setFilterOnFilename(Boolean filterOnFilename)
    {
        this.filterOnFilename = filterOnFilename;
    }

    public Boolean getFilterOnLastModifiedDate()
    {
        return filterOnLastModifiedDate;
    }

    public void setFilterOnLastModifiedDate(Boolean filterOnLastModifiedDate)
    {
        this.filterOnLastModifiedDate = filterOnLastModifiedDate;
    }

    public Boolean getRenameOnSuccess()
    {
        return renameOnSuccess;
    }

    public void setRenameOnSuccess(Boolean renameOnSuccess)
    {
        this.renameOnSuccess = renameOnSuccess;
    }

    public String getRenameOnSuccessExtension()
    {
        return renameOnSuccessExtension;
    }

    public void setRenameOnSuccessExtension(String renameOnSuccessExtension)
    {
        this.renameOnSuccessExtension = renameOnSuccessExtension;
    }

    public Boolean getMoveOnSuccess()
    {
        return moveOnSuccess;
    }

    public void setMoveOnSuccess(Boolean moveOnSuccess)
    {
        this.moveOnSuccess = moveOnSuccess;
    }

    public String getMoveOnSuccessNewPath()
    {
        return moveOnSuccessNewPath;
    }

    public void setMoveOnSuccessNewPath(String moveOnSuccessNewPath)
    {
        this.moveOnSuccessNewPath = moveOnSuccessNewPath;
    }

    public Boolean getChronological()
    {
        return chronological;
    }

    public void setChronological(Boolean chronological)
    {
        this.chronological = chronological;
    }

    public Boolean getChunking()
    {
        return chunking;
    }

    public void setChunking(Boolean chunking)
    {
        this.chunking = chunking;
    }

    public Integer getChunkSize()
    {
        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize)
    {
        this.chunkSize = chunkSize;
    }

    public Boolean getChecksum()
    {
        return checksum;
    }

    public void setChecksum(Boolean checksum)
    {
        this.checksum = checksum;
    }

    public Long getMinAge()
    {
        return minAge;
    }

    public void setMinAge(Long minAge)
    {
        this.minAge = minAge;
    }

    public Boolean getDestructive()
    {
        return destructive;
    }

    public void setDestructive(Boolean destructive)
    {
        this.destructive = destructive;
    }

    public Integer getMaxRows()
    {
        return maxRows;
    }

    public void setMaxRows(Integer maxRows)
    {
        this.maxRows = maxRows;
    }

    public Integer getAgeOfFiles()
    {
        return ageOfFiles;
    }

    public void setAgeOfFiles(Integer ageOfFiles)
    {
        this.ageOfFiles = ageOfFiles;
    }

    public String getClientID()
    {
        return clientID;
    }

    public void setClientID(String clientID)
    {
        this.clientID = clientID;
    }

    public Boolean getCleanupJournalOnComplete()
    {
        return cleanupJournalOnComplete;
    }

    public void setCleanupJournalOnComplete(Boolean cleanupJournalOnComplete)
    {
        this.cleanupJournalOnComplete = cleanupJournalOnComplete;
    }

    public String getRemoteHost()
    {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost)
    {
        this.remoteHost = remoteHost;
    }

    public String getPrivateKeyFilename()
    {
        return privateKeyFilename;
    }

    public void setPrivateKeyFilename(String privateKeyFilename)
    {
        this.privateKeyFilename = privateKeyFilename;
    }

    public Integer getMaxRetryAttempts()
    {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(Integer maxRetryAttempts)
    {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public Integer getRemotePort()
    {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort)
    {
        this.remotePort = remotePort;
    }

    public String getKnownHostsFilename()
    {
        return knownHostsFilename;
    }

    public void setKnownHostsFilename(String knownHostsFilename)
    {
        this.knownHostsFilename = knownHostsFilename;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Integer getConnectionTimeout()
    {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    
}
