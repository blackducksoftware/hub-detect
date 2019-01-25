package com.blackducksoftware.integration.hub.detect.workflow.search;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class DetectorExclusionSearchFilter implements DetectorSearchFilter {
    private List<String> excludedDirectories;
    private WildcardFileFilter fileFilter;

    public DetectorExclusionSearchFilter(List<String> excludedDirectories, List<String> excludedDirectoryNamePatterns){
        this.excludedDirectories = excludedDirectories;
        fileFilter = new WildcardFileFilter(excludedDirectoryNamePatterns);
    }

    @Override
    public boolean shouldExclude(File file) {
        for (final String excludedDirectory : excludedDirectories) {
            if (FilenameUtils.wildcardMatchOnSystem(file.getName(), excludedDirectory)) {
                return true;
            }
        }

        return fileFilter.accept(file); //returns TRUE if it matches one of the file filters.
    }
}