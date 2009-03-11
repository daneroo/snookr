
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVDispatcher.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVGuideUpdater.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVLibrary.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVLicenseManager.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVScheduler.asmx?WSDL");
#
#    daniel@LASCALA /cygdrive/c/Program Files/SnapStream Media/Beyond TV/wwwroot/wsdl
#    $ ls
#    BTVBatchProcessor.asmx	BTVLibrary.asmx		BTVScheduler.asmx
#    BTVDispatcher.asmx	BTVLicenseManager.asmx	BTVSettings.asmx
#    BTVExpiration.asmx	BTVLiveTVManager.asmx	BTVWebServiceManager.asmx
#    BTVGuideData.asmx	BTVLog.asmx		InternalBTVScheduler.asmx
#    BTVGuideUpdater.asmx	BTVNotifier.asmx



-=-=-=-=-=   API for BTVBatchProcessor

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( BTVTaskListProcessor ) tns="http://www.snapstream.com/WebService"
   Prefixes (3)
      ns0 = "http://microsoft.com/wsdl/types/"
      ns1 = "http://www.snapstream.com/WebService"
      ns2 = "http://www.snapstream.com/types"
   Ports (2):
      (BTVTaskListProcessorSoap)
         Methods (29):
            GetCount(xs:string authTicket, )
            GetEnableTimeConstraint(xs:string authTicket, )
            GetNumberOfTasksInProgress(xs:string authTicket, )
            GetNumberOfTasksPending(xs:string authTicket, )
            GetTaskByFullName(xs:string authTicket, xs:string taskName, xs:string fullName, xs:unsignedInt index, )
            GetTimeConstraint(xs:string authTicket, )
            Halt(xs:string authTicket, ns0:guid guid, )
            IsTranscoding(xs:string authTicket, )
            ItemByID(xs:string authTicket, ns0:guid guid, )
            ItemByIndex(xs:string authTicket, xs:unsignedInt index, )
            ProgressByID(xs:string authTicket, ns0:guid guid, )
            Remove(xs:string authTicket, ns0:guid guid, )
            RemoveByName(xs:string authTicket, ns0:guid guid, xs:string taskName, xs:unsignedInt startingIndex, )
            Reset(xs:string authTicket, ns0:guid guid, )
            Run(xs:string authTicket, ns0:guid guid, )
            SetEnableTimeConstraint(xs:string authTicket, xs:int val, )
            SetTimeConstraint(xs:string authTicket, xs:string bstrStartXml, xs:string bstrEndXml, )
            SubmitBatchClip(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            SubmitBatchClipAndSqueeze(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            SubmitBatchRecompress(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            SubmitChapterList(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SubmitClip(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SubmitClipAndSqueeze(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SubmitConvertSSXToAVI(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SubmitEmail(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SubmitFileTag(xs:string authTicket, xs:string bstrXMLProps, )
            SubmitRecompress(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SubmitTPSnip(xs:string authTicket, ns2:PVSPropertyBag bag, )
            Wait(xs:string authTicket, ns0:guid guid, )
         Types (6):
            ns2:ArrayOfPVSProperty
            ArrayOfPVSPropertyBag
            ArrayOfString
            ns2:PVSProperty
            ns2:PVSPropertyBag
            ns0:guid
      (BTVTaskListProcessorSoap12)
         Methods (29):
            GetCount(xs:string authTicket, )
            GetEnableTimeConstraint(xs:string authTicket, )
            GetNumberOfTasksInProgress(xs:string authTicket, )
            GetNumberOfTasksPending(xs:string authTicket, )
            GetTaskByFullName(xs:string authTicket, xs:string taskName, xs:string fullName, xs:unsignedInt index, )
            GetTimeConstraint(xs:string authTicket, )
            Halt(xs:string authTicket, ns0:guid guid, )
            IsTranscoding(xs:string authTicket, )
            ItemByID(xs:string authTicket, ns0:guid guid, )
            ItemByIndex(xs:string authTicket, xs:unsignedInt index, )
            ProgressByID(xs:string authTicket, ns0:guid guid, )
            Remove(xs:string authTicket, ns0:guid guid, )
            RemoveByName(xs:string authTicket, ns0:guid guid, xs:string taskName, xs:unsignedInt startingIndex, )
            Reset(xs:string authTicket, ns0:guid guid, )
            Run(xs:string authTicket, ns0:guid guid, )
            SetEnableTimeConstraint(xs:string authTicket, xs:int val, )
            SetTimeConstraint(xs:string authTicket, xs:string bstrStartXml, xs:string bstrEndXml, )
            SubmitBatchClip(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            SubmitBatchClipAndSqueeze(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            SubmitBatchRecompress(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            SubmitChapterList(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SubmitClip(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SubmitClipAndSqueeze(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SubmitConvertSSXToAVI(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SubmitEmail(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SubmitFileTag(xs:string authTicket, xs:string bstrXMLProps, )
            SubmitRecompress(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SubmitTPSnip(xs:string authTicket, ns2:PVSPropertyBag bag, )
            Wait(xs:string authTicket, ns0:guid guid, )
         Types (6):
            ns2:ArrayOfPVSProperty
            ArrayOfPVSPropertyBag
            ArrayOfString
            ns2:PVSProperty
            ns2:PVSPropertyBag
            ns0:guid




-=-=-=-=-=   API for BTVLibrary

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( BTVLibrary ) tns="http://www.snapstream.com/WebService"
   Prefixes (3)
      ns0 = "http://microsoft.com/wsdl/types/"
      ns1 = "http://www.snapstream.com/WebService"
      ns2 = "http://www.snapstream.com/types"
   Ports (2):
      (BTVLibrarySoap)
         Methods (39):
            AddFolder(xs:string authTicket, xs:string sFolder, )
            AddFolder2(xs:string authTicket, xs:string sFolder, xs:boolean bRecurse, )
            AddFolder3(xs:string authTicket, xs:string sFolder, xs:boolean bRecurse, xs:boolean bManaged, )
            AdvancedSearchCC(xs:string authTicket, xs:string search, xs:int pageNumber, xs:int resultsPerPage, xs:boolean titleSearch, xs:boolean descSearch, xs:boolean ccSearch, xs:string highlightStart, xs:string highlightEnd, )
            AdvancedSearchCCCount(xs:string authTicket, xs:string search, xs:boolean titleSearch, xs:boolean descSearch, xs:boolean ccSearch, )
            AdvancedSearchCCCountWithSuggestion(xs:string authTicket, xs:string search, xs:boolean titleSearch, xs:boolean descSearch, xs:boolean ccSearch, xs:string suggestionStart, xs:string suggestionEnd, )
            AdvancedSearchCCWithSuggestion(xs:string authTicket, xs:string search, xs:int pageNumber, xs:int resultsPerPage, xs:boolean titleSearch, xs:boolean descSearch, xs:boolean ccSearch, xs:string highlightStart, xs:string highlightEnd, xs:string suggestionStart, xs:string suggestionEnd, )
            DeleteMedia(xs:string authTicket, xs:string fullName, )
            EditMedia(xs:string authTicket, xs:string fullName, ns2:PVSPropertyBag bag, )
            FlatViewByDate(xs:string authTicket, )
            FlatViewByDate2(xs:string authTicket, xs:int index, xs:int count, )
            FlatViewByTitle(xs:string authTicket, )
            FlatViewClipsByDate(xs:string authTicket, )
            GetAllClipsWithFilter(xs:string authTicket, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, )
            GetAllMediaWithFilter(xs:string authTicket, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, )
            GetClips(xs:string authTicket, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, xs:int start, xs:int count, )
            GetDriveInformationByName(xs:string authTicket, xs:string driveName, )
            GetFolder(xs:string authTicket, xs:string folderName, )
            GetFolderMediaCount(xs:string authTicket, xs:string folderName, )
            GetFolderWithFilter(xs:string authTicket, xs:string folder, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, xs:int start, xs:int count, )
            GetFolders(xs:string authTicket, xs:string folderName, )
            GetFoldersByRange(xs:string authTicket, xs:string folderName, xs:int start, xs:int count, )
            GetItemsByFolder(xs:string authTicket, xs:string folder, )
            GetItemsByFolderWithFilter(xs:string authTicket, xs:string folder, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, )
            GetItemsByJob(xs:string authTicket, ns0:guid job, )
            GetItemsBySeries(xs:string authTicket, xs:string series, )
            GetItemsBySeriesWithFilter(xs:string authTicket, xs:string series, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, )
            GetLastChange(xs:string authTicket, )
            GetMedia(xs:string authTicket, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, xs:int start, xs:int count, )
            GetMediaByFullName(xs:string authTicket, xs:string fullName, )
            GetMyClips(xs:string authTicket, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, xs:int start, xs:int count, )
            GetOldestUnlockedMediaDateTicks(xs:string authTicket, )
            GetSeries(xs:string authTicket, )
            GetSeriesByRange(xs:string authTicket, xs:int start, xs:int count, )
            GetSeriesWithFilter(xs:string authTicket, xs:string series, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, xs:int start, xs:int count, )
            GetStatsInformation(xs:string authTicket, )
            LuceneHighlightString(xs:string authTicket, xs:string inputString, xs:string query, xs:string highlightStart, xs:string highlightEnd, )
            RemoveFolder(xs:string authTicket, xs:string sFolder, )
            SetUserSpecificProperty(xs:string authTicket, xs:string fullName, xs:string property, xs:string value, )
         Types (8):
            ns2:ArrayOfPVSProperty
            ArrayOfPVSPropertyBag
            LibraryHideOption
            LibrarySort
            LibrarySortOrder
            ns2:PVSProperty
            ns2:PVSPropertyBag
            ns0:guid
      (BTVLibrarySoap12)
         Methods (39):
            AddFolder(xs:string authTicket, xs:string sFolder, )
            AddFolder2(xs:string authTicket, xs:string sFolder, xs:boolean bRecurse, )
            AddFolder3(xs:string authTicket, xs:string sFolder, xs:boolean bRecurse, xs:boolean bManaged, )
            AdvancedSearchCC(xs:string authTicket, xs:string search, xs:int pageNumber, xs:int resultsPerPage, xs:boolean titleSearch, xs:boolean descSearch, xs:boolean ccSearch, xs:string highlightStart, xs:string highlightEnd, )
            AdvancedSearchCCCount(xs:string authTicket, xs:string search, xs:boolean titleSearch, xs:boolean descSearch, xs:boolean ccSearch, )
            AdvancedSearchCCCountWithSuggestion(xs:string authTicket, xs:string search, xs:boolean titleSearch, xs:boolean descSearch, xs:boolean ccSearch, xs:string suggestionStart, xs:string suggestionEnd, )
            AdvancedSearchCCWithSuggestion(xs:string authTicket, xs:string search, xs:int pageNumber, xs:int resultsPerPage, xs:boolean titleSearch, xs:boolean descSearch, xs:boolean ccSearch, xs:string highlightStart, xs:string highlightEnd, xs:string suggestionStart, xs:string suggestionEnd, )
            DeleteMedia(xs:string authTicket, xs:string fullName, )
            EditMedia(xs:string authTicket, xs:string fullName, ns2:PVSPropertyBag bag, )
            FlatViewByDate(xs:string authTicket, )
            FlatViewByDate2(xs:string authTicket, xs:int index, xs:int count, )
            FlatViewByTitle(xs:string authTicket, )
            FlatViewClipsByDate(xs:string authTicket, )
            GetAllClipsWithFilter(xs:string authTicket, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, )
            GetAllMediaWithFilter(xs:string authTicket, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, )
            GetClips(xs:string authTicket, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, xs:int start, xs:int count, )
            GetDriveInformationByName(xs:string authTicket, xs:string driveName, )
            GetFolder(xs:string authTicket, xs:string folderName, )
            GetFolderMediaCount(xs:string authTicket, xs:string folderName, )
            GetFolderWithFilter(xs:string authTicket, xs:string folder, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, xs:int start, xs:int count, )
            GetFolders(xs:string authTicket, xs:string folderName, )
            GetFoldersByRange(xs:string authTicket, xs:string folderName, xs:int start, xs:int count, )
            GetItemsByFolder(xs:string authTicket, xs:string folder, )
            GetItemsByFolderWithFilter(xs:string authTicket, xs:string folder, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, )
            GetItemsByJob(xs:string authTicket, ns0:guid job, )
            GetItemsBySeries(xs:string authTicket, xs:string series, )
            GetItemsBySeriesWithFilter(xs:string authTicket, xs:string series, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, )
            GetLastChange(xs:string authTicket, )
            GetMedia(xs:string authTicket, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, xs:int start, xs:int count, )
            GetMediaByFullName(xs:string authTicket, xs:string fullName, )
            GetMyClips(xs:string authTicket, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, xs:int start, xs:int count, )
            GetOldestUnlockedMediaDateTicks(xs:string authTicket, )
            GetSeries(xs:string authTicket, )
            GetSeriesByRange(xs:string authTicket, xs:int start, xs:int count, )
            GetSeriesWithFilter(xs:string authTicket, xs:string series, LibraryHideOption hide, LibrarySort sort, LibrarySortOrder order, xs:int start, xs:int count, )
            GetStatsInformation(xs:string authTicket, )
            LuceneHighlightString(xs:string authTicket, xs:string inputString, xs:string query, xs:string highlightStart, xs:string highlightEnd, )
            RemoveFolder(xs:string authTicket, xs:string sFolder, )
            SetUserSpecificProperty(xs:string authTicket, xs:string fullName, xs:string property, xs:string value, )
         Types (8):
            ns2:ArrayOfPVSProperty
            ArrayOfPVSPropertyBag
            LibraryHideOption
            LibrarySort
            LibrarySortOrder
            ns2:PVSProperty
            ns2:PVSPropertyBag
            ns0:guid




-=-=-=-=-=   API for BTVScheduler

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( BTVScheduler ) tns="http://www.snapstream.com/WebService"
   Prefixes (3)
      ns0 = "http://microsoft.com/wsdl/types/"
      ns1 = "http://www.snapstream.com/WebService"
      ns2 = "http://www.snapstream.com/types"
   Ports (2):
      (BTVSchedulerSoap)
         Methods (43):
            AddBlockedRecordingByGUID(xs:string authTicket, ns0:guid guid, )
            AddBlockedRecordings(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            AddForcedRecordingByGUID(xs:string authTicket, ns0:guid guid, )
            AddForcedRecordings(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            AddRecentRecordingByGUID(xs:string authTicket, ns0:guid guid, )
            AddRecentRecordings(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            AddRecordingJob(xs:string authTicket, ns2:PVSPropertyBag bag, xs:int highestPriority, )
            AddRecordingJobs(xs:string authTicket, ArrayOfPVSPropertyBag bags, xs:int highestPriority, )
            ClearPopularityJobs(xs:string authTicket, )
            EditRecordingJob(xs:string authTicket, ns0:guid guid, ns2:PVSPropertyBag bag, )
            EditRecordingJobs(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            GetBlockedRecordings(xs:string authTicket, )
            GetEpisodeCollectionStatus(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            GetEpisodeStatus(xs:string authTicket, ns2:PVSPropertyBag bag, )
            GetExistingConflicts(xs:string authTicket, ns0:guid guid, )
            GetForcedRecordings(xs:string authTicket, )
            GetJobByGUID(xs:string authTicket, ns0:guid guid, )
            GetJobs(xs:string authTicket, )
            GetLastRebuild(xs:string authTicket, )
            GetLastRecording(xs:string authTicket, )
            GetNextRecording(xs:string authTicket, )
            GetPossibleRecordings(xs:string authTicket, )
            GetPotentialConflicts(xs:string authTicket, ns2:PVSPropertyBag bag, )
            GetPotentialRecordings(xs:string authTicket, ns2:PVSPropertyBag bag, )
            GetPotentialScheduleAtPriority(xs:string authTicket, ns2:PVSPropertyBag bag, xs:unsignedInt priority, )
            GetRecentRecordings(xs:string authTicket, )
            GetRecordings(xs:string authTicket, ns0:guid guid, )
            GetRejectedRecordings(xs:string authTicket, )
            GetUpcomingRecordingDuration(xs:string authTicket, )
            GetUpcomingRecordings(xs:string authTicket, )
            HasExistingConflicts(xs:string authTicket, ns0:guid guid, )
            HasPotentialConflicts(xs:string authTicket, ns2:PVSPropertyBag bag, )
            IsSeriesManaged(xs:string authTicket, xs:string bstrEPGID, )
            RemoveBlockedRecordings(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            RemoveBlockedRecordingsByGUID(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            RemoveForcedRecordings(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            RemoveForcedRecordingsByGUID(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            RemoveRecentRecordings(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            RemoveRecentRecordingsByGUID(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            RemoveRecordingJob(xs:string authTicket, ns0:guid guid, )
            RemoveRecordingJobs(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            ReprioritizeJob(xs:string authTicket, ns0:guid guid, xs:unsignedInt priority, )
            ReprioritizeJobs(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
         Types (8):
            ns2:ArrayOfPVSProperty
            ns2:ArrayOfPVSPropertyBag
            ArrayOfPVSPropertyBag
            ArrayOfPVSPropertyBagArray
            ns2:PVSProperty
            ns2:PVSPropertyBag
            ns2:PVSPropertyBagArray
            ns0:guid
      (BTVSchedulerSoap12)
         Methods (43):
            AddBlockedRecordingByGUID(xs:string authTicket, ns0:guid guid, )
            AddBlockedRecordings(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            AddForcedRecordingByGUID(xs:string authTicket, ns0:guid guid, )
            AddForcedRecordings(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            AddRecentRecordingByGUID(xs:string authTicket, ns0:guid guid, )
            AddRecentRecordings(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            AddRecordingJob(xs:string authTicket, ns2:PVSPropertyBag bag, xs:int highestPriority, )
            AddRecordingJobs(xs:string authTicket, ArrayOfPVSPropertyBag bags, xs:int highestPriority, )
            ClearPopularityJobs(xs:string authTicket, )
            EditRecordingJob(xs:string authTicket, ns0:guid guid, ns2:PVSPropertyBag bag, )
            EditRecordingJobs(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            GetBlockedRecordings(xs:string authTicket, )
            GetEpisodeCollectionStatus(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            GetEpisodeStatus(xs:string authTicket, ns2:PVSPropertyBag bag, )
            GetExistingConflicts(xs:string authTicket, ns0:guid guid, )
            GetForcedRecordings(xs:string authTicket, )
            GetJobByGUID(xs:string authTicket, ns0:guid guid, )
            GetJobs(xs:string authTicket, )
            GetLastRebuild(xs:string authTicket, )
            GetLastRecording(xs:string authTicket, )
            GetNextRecording(xs:string authTicket, )
            GetPossibleRecordings(xs:string authTicket, )
            GetPotentialConflicts(xs:string authTicket, ns2:PVSPropertyBag bag, )
            GetPotentialRecordings(xs:string authTicket, ns2:PVSPropertyBag bag, )
            GetPotentialScheduleAtPriority(xs:string authTicket, ns2:PVSPropertyBag bag, xs:unsignedInt priority, )
            GetRecentRecordings(xs:string authTicket, )
            GetRecordings(xs:string authTicket, ns0:guid guid, )
            GetRejectedRecordings(xs:string authTicket, )
            GetUpcomingRecordingDuration(xs:string authTicket, )
            GetUpcomingRecordings(xs:string authTicket, )
            HasExistingConflicts(xs:string authTicket, ns0:guid guid, )
            HasPotentialConflicts(xs:string authTicket, ns2:PVSPropertyBag bag, )
            IsSeriesManaged(xs:string authTicket, xs:string bstrEPGID, )
            RemoveBlockedRecordings(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            RemoveBlockedRecordingsByGUID(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            RemoveForcedRecordings(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            RemoveForcedRecordingsByGUID(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            RemoveRecentRecordings(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            RemoveRecentRecordingsByGUID(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            RemoveRecordingJob(xs:string authTicket, ns0:guid guid, )
            RemoveRecordingJobs(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            ReprioritizeJob(xs:string authTicket, ns0:guid guid, xs:unsignedInt priority, )
            ReprioritizeJobs(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
         Types (8):
            ns2:ArrayOfPVSProperty
            ns2:ArrayOfPVSPropertyBag
            ArrayOfPVSPropertyBag
            ArrayOfPVSPropertyBagArray
            ns2:PVSProperty
            ns2:PVSPropertyBag
            ns2:PVSPropertyBagArray
            ns0:guid




-=-=-=-=-=   API for BTVDispatcher

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( BTVDispatcher ) tns="http://www.snapstream.com/WebService"
   Prefixes (2)
      ns0 = "http://www.snapstream.com/WebService"
      ns1 = "http://www.snapstream.com/types"
   Ports (2):
      (BTVDispatcherSoap)
         Methods (5):
            GetActiveRecordings(xs:string authTicket, )
            GetActiveRecordingsWithChannel(xs:string authTicket, xs:string uniqueChannelID, )
            GetEngines(xs:string authTicket, )
            GetEnginesWithChannel(xs:string authTicket, xs:string uniqueChannelID, )
            SetSources(xs:string authTicket, ArrayOfPVSPropertyBag sources, )
         Types (4):
            ns1:ArrayOfPVSProperty
            ArrayOfPVSPropertyBag
            ns1:PVSProperty
            ns1:PVSPropertyBag
      (BTVDispatcherSoap12)
         Methods (5):
            GetActiveRecordings(xs:string authTicket, )
            GetActiveRecordingsWithChannel(xs:string authTicket, xs:string uniqueChannelID, )
            GetEngines(xs:string authTicket, )
            GetEnginesWithChannel(xs:string authTicket, xs:string uniqueChannelID, )
            SetSources(xs:string authTicket, ArrayOfPVSPropertyBag sources, )
         Types (4):
            ns1:ArrayOfPVSProperty
            ArrayOfPVSPropertyBag
            ns1:PVSProperty
            ns1:PVSPropertyBag




-=-=-=-=-=   API for BTVLicenseManager

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( BTVLicenseManager ) tns="http://www.snapstream.com/WebService"
   Prefixes (1)
      ns0 = "http://www.snapstream.com/types"
   Ports (2):
      (BTVLicenseManagerSoap)
         Methods (8):
            CanChangePassword(xs:string authTicket, )
            ChangePassword(xs:string authTicket, xs:string oldPassword, xs:string newPassword, )
            GetPermissionsForTicket(xs:string authTicket, )
            GetVersionNumber()
            Logoff(xs:string ticket, )
            Logon(xs:string networkLicense, xs:string username, xs:string password, )
            LogonRemote(xs:string networkLicense, xs:string username, xs:string password, )
            RenewLogonSession(xs:string authTicket, )
         Types (3):
            ns0:ArrayOfPVSProperty
            ns0:PVSProperty
            ns0:PVSPropertyBag
      (BTVLicenseManagerSoap12)
         Methods (8):
            CanChangePassword(xs:string authTicket, )
            ChangePassword(xs:string authTicket, xs:string oldPassword, xs:string newPassword, )
            GetPermissionsForTicket(xs:string authTicket, )
            GetVersionNumber()
            Logoff(xs:string ticket, )
            Logon(xs:string networkLicense, xs:string username, xs:string password, )
            LogonRemote(xs:string networkLicense, xs:string username, xs:string password, )
            RenewLogonSession(xs:string authTicket, )
         Types (3):
            ns0:ArrayOfPVSProperty
            ns0:PVSProperty
            ns0:PVSPropertyBag




-=-=-=-=-=   API for BTVSettings

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( BTVSettings ) tns="http://www.snapstream.com/WebService"
   Prefixes (2)
      ns0 = "http://www.snapstream.com/WebService"
      ns1 = "http://www.snapstream.com/types"
   Ports (2):
      (BTVSettingsSoap)
         Methods (23):
            GetBaseLineups(xs:string authTicket, )
            GetDevices(xs:string authTicket, )
            GetFeature(xs:string authTicket, xs:string name, )
            GetNetURL(xs:string authTicket, )
            GetOEMCode(xs:string authTicket, )
            GetProfiles(xs:string authTicket, )
            GetProperty(xs:string authTicket, xs:string name, )
            GetPropertyAsBool(xs:string authTicket, xs:string name, )
            GetPropertyAsDouble(xs:string authTicket, xs:string name, )
            GetPropertyAsInt(xs:string authTicket, xs:string name, )
            GetPropertyBagCollection(xs:string authTicket, xs:string name, )
            GetRadioLineup(xs:string authTicket, )
            GetUnifiedLineup(xs:string authTicket, )
            GetUnifiedLineupDisplayedChannels(xs:string authTicket, )
            GetUserLineups(xs:string authTicket, )
            GetValidRecordingFormats(xs:string authTicket, )
            SetDevices(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            SetProfiles(xs:string authTicket, ArrayOfPVSPropertyBag bags, xs:int bSaveFile, )
            SetProperty(xs:string authTicket, xs:string name, xs:string valueString, )
            SetPropertyAsBool(xs:string authTicket, xs:string name, xs:boolean val, )
            SetPropertyAsDouble(xs:string authTicket, xs:string name, xs:double val, )
            SetPropertyAsInt(xs:string authTicket, xs:string name, xs:unsignedInt val, )
            WriteSettingsToDisk(xs:string authTicket, )
         Types (4):
            ns1:ArrayOfPVSProperty
            ArrayOfPVSPropertyBag
            ns1:PVSProperty
            ns1:PVSPropertyBag
      (BTVSettingsSoap12)
         Methods (23):
            GetBaseLineups(xs:string authTicket, )
            GetDevices(xs:string authTicket, )
            GetFeature(xs:string authTicket, xs:string name, )
            GetNetURL(xs:string authTicket, )
            GetOEMCode(xs:string authTicket, )
            GetProfiles(xs:string authTicket, )
            GetProperty(xs:string authTicket, xs:string name, )
            GetPropertyAsBool(xs:string authTicket, xs:string name, )
            GetPropertyAsDouble(xs:string authTicket, xs:string name, )
            GetPropertyAsInt(xs:string authTicket, xs:string name, )
            GetPropertyBagCollection(xs:string authTicket, xs:string name, )
            GetRadioLineup(xs:string authTicket, )
            GetUnifiedLineup(xs:string authTicket, )
            GetUnifiedLineupDisplayedChannels(xs:string authTicket, )
            GetUserLineups(xs:string authTicket, )
            GetValidRecordingFormats(xs:string authTicket, )
            SetDevices(xs:string authTicket, ArrayOfPVSPropertyBag bags, )
            SetProfiles(xs:string authTicket, ArrayOfPVSPropertyBag bags, xs:int bSaveFile, )
            SetProperty(xs:string authTicket, xs:string name, xs:string valueString, )
            SetPropertyAsBool(xs:string authTicket, xs:string name, xs:boolean val, )
            SetPropertyAsDouble(xs:string authTicket, xs:string name, xs:double val, )
            SetPropertyAsInt(xs:string authTicket, xs:string name, xs:unsignedInt val, )
            WriteSettingsToDisk(xs:string authTicket, )
         Types (4):
            ns1:ArrayOfPVSProperty
            ArrayOfPVSPropertyBag
            ns1:PVSProperty
            ns1:PVSPropertyBag




-=-=-=-=-=   API for BTVExpiration

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( BTVExpiration ) tns="http://www.snapstream.com/WebService"
   Prefixes (3)
      ns0 = "http://microsoft.com/wsdl/types/"
      ns1 = "http://www.snapstream.com/WebService"
      ns2 = "http://www.snapstream.com/types"
   Ports (2):
      (BTVExpirationSoap)
         Methods (6):
            GetDriveLimitsByName(xs:string authTicket, xs:string name, )
            GetMaxDaysByGuid(xs:string authTicket, ns0:guid guid, )
            GetShowLimitsByGuid(xs:string authTicket, ns0:guid guid, )
            SetDaysLimitsByGuid(xs:string authTicket, ns0:guid guid, xs:int days, )
            SetDriveLimits(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SetShowLimitsByGuid(xs:string authTicket, ns0:guid guid, xs:int limit, )
         Types (4):
            ns2:ArrayOfPVSProperty
            ns2:PVSProperty
            ns2:PVSPropertyBag
            ns0:guid
      (BTVExpirationSoap12)
         Methods (6):
            GetDriveLimitsByName(xs:string authTicket, xs:string name, )
            GetMaxDaysByGuid(xs:string authTicket, ns0:guid guid, )
            GetShowLimitsByGuid(xs:string authTicket, ns0:guid guid, )
            SetDaysLimitsByGuid(xs:string authTicket, ns0:guid guid, xs:int days, )
            SetDriveLimits(xs:string authTicket, ns2:PVSPropertyBag bag, )
            SetShowLimitsByGuid(xs:string authTicket, ns0:guid guid, xs:int limit, )
         Types (4):
            ns2:ArrayOfPVSProperty
            ns2:PVSProperty
            ns2:PVSPropertyBag
            ns0:guid




-=-=-=-=-=   API for BTVLiveTVManager

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( BTVLiveTVManager ) tns="http://www.snapstream.com/WebService"
   Prefixes (3)
      ns0 = "http://microsoft.com/wsdl/types/"
      ns1 = "http://www.snapstream.com/WebService"
      ns2 = "http://www.snapstream.com/types"
   Ports (2):
      (BTVLiveTVManagerSoap)
         Methods (21):
            EndSession(xs:string authTicket, ns0:guid guid, )
            ExistingSession(xs:string authTicket, xs:string sessionType, ns0:guid existingGuid, )
            ExistingSourceSession(xs:string authTicket, ns0:guid sourceGuid, )
            ExistingWebSession(xs:string authTicket, ns0:guid sourceGuid, )
            ForceChannel(xs:string authTicket, ns0:guid guid, ns0:guid guidSource, xs:string channel, )
            GetChannel(xs:string authTicket, ns0:guid guid, )
            GetImminentRecordings(xs:string authTicket, ns0:guid guid, )
            GetRecording(xs:string authTicket, ns0:guid guid, )
            GetSessionProps(xs:string authTicket, ns0:guid guid, )
            GetSessions(xs:string authTicket, )
            GetSignalStrength(xs:string authTicket, ns0:guid guid, )
            IsRecording(xs:string authTicket, ns0:guid guid, )
            KeepAlive(xs:string authTicket, ns0:guid guid, )
            NewSession(xs:string authTicket, xs:string sessionType, xs:string uniqueChannelId, )
            NewSessionOnSource(xs:string authTicket, xs:string sessionType, ns0:guid guidSource, )
            NewSessionWithErrorCondition(xs:string authTicket, xs:string sessionType, xs:string uniqueChannelID, )
            NewSourceSession(xs:string authTicket, )
            NewSourceSessionOnChannel(xs:string authTicket, xs:string channelID, )
            NewWebSession(xs:string authTicket, )
            SetChannel(xs:string authTicket, ns0:guid guid, xs:string channel, )
            SetChannelWithErrorCondition(xs:string authTicket, ns0:guid guid, xs:string channel, )
         Types (6):
            ArrayOfAnyType
            ns2:ArrayOfPVSProperty
            ArrayOfPVSPropertyBag
            ns2:PVSProperty
            ns2:PVSPropertyBag
            ns0:guid
      (BTVLiveTVManagerSoap12)
         Methods (21):
            EndSession(xs:string authTicket, ns0:guid guid, )
            ExistingSession(xs:string authTicket, xs:string sessionType, ns0:guid existingGuid, )
            ExistingSourceSession(xs:string authTicket, ns0:guid sourceGuid, )
            ExistingWebSession(xs:string authTicket, ns0:guid sourceGuid, )
            ForceChannel(xs:string authTicket, ns0:guid guid, ns0:guid guidSource, xs:string channel, )
            GetChannel(xs:string authTicket, ns0:guid guid, )
            GetImminentRecordings(xs:string authTicket, ns0:guid guid, )
            GetRecording(xs:string authTicket, ns0:guid guid, )
            GetSessionProps(xs:string authTicket, ns0:guid guid, )
            GetSessions(xs:string authTicket, )
            GetSignalStrength(xs:string authTicket, ns0:guid guid, )
            IsRecording(xs:string authTicket, ns0:guid guid, )
            KeepAlive(xs:string authTicket, ns0:guid guid, )
            NewSession(xs:string authTicket, xs:string sessionType, xs:string uniqueChannelId, )
            NewSessionOnSource(xs:string authTicket, xs:string sessionType, ns0:guid guidSource, )
            NewSessionWithErrorCondition(xs:string authTicket, xs:string sessionType, xs:string uniqueChannelID, )
            NewSourceSession(xs:string authTicket, )
            NewSourceSessionOnChannel(xs:string authTicket, xs:string channelID, )
            NewWebSession(xs:string authTicket, )
            SetChannel(xs:string authTicket, ns0:guid guid, xs:string channel, )
            SetChannelWithErrorCondition(xs:string authTicket, ns0:guid guid, xs:string channel, )
         Types (6):
            ArrayOfAnyType
            ns2:ArrayOfPVSProperty
            ArrayOfPVSPropertyBag
            ns2:PVSProperty
            ns2:PVSPropertyBag
            ns0:guid




-=-=-=-=-=   API for BTVWebServiceManager

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( BTVWebServiceManager ) tns="http://www.snapstream.com/WebService"
   Prefixes (1)
      ns0 = "http://www.snapstream.com/WebService"
   Ports (2):
      (BTVWebServiceManagerSoap)
         Methods (4):
            Repair(xs:string authTicket, )
            Shutdown(xs:string authTicket, )
            Startup(xs:string authTicket, )
            Terminate(xs:string authTicket, )
         Types (1):
            ArrayOfString
      (BTVWebServiceManagerSoap12)
         Methods (4):
            Repair(xs:string authTicket, )
            Shutdown(xs:string authTicket, )
            Startup(xs:string authTicket, )
            Terminate(xs:string authTicket, )
         Types (1):
            ArrayOfString




-=-=-=-=-=   API for BTVGuideData

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( BTVGuideData ) tns="http://www.snapstream.com/WebService"
   Prefixes (2)
      ns0 = "http://www.snapstream.com/WebService"
      ns1 = "http://www.snapstream.com/types"
   Ports (2):
      (BTVGuideDataSoap)
         Methods (26):
            GetCategories(xs:string authTicket, xs:string bstrCategory, )
            GetDataExtents(xs:string authTicket, )
            GetEpisodeByStationAndTime(xs:string authTicket, xs:string uniqueChannelID, xs:unsignedLong time, )
            GetEpisodesByKeyword(xs:string authTicket, xs:string keyword, )
            GetEpisodesByKeywordWithLimit(xs:string authTicket, xs:string keyword, xs:unsignedInt limit, )
            GetEpisodesByKeywordWithOptions(xs:string authTicket, xs:string keyword, xs:int titleSearch, xs:int descSearch, xs:int actorSearch, xs:unsignedInt limit, )
            GetEpisodesByRange2(xs:string authTicket, xs:string uniqueChannelIDStart, xs:string uniqueChannelIDEnd, xs:unsignedLong timeStart, xs:unsignedLong timeEnd, )
            GetEpisodesByRange3(xs:string authTicket, ArrayOfString channels, xs:unsignedLong timeStart, xs:unsignedLong timeEnd, )
            GetEpisodesBySeriesID(xs:string authTicket, xs:string epgID, )
            GetEpisodesByStation(xs:string authTicket, xs:string uniqueChannelID, )
            GetEpisodesByStationAndSeriesID(xs:string authTicket, xs:string uniqueChannelID, xs:string epgID, xs:unsignedLong time, )
            GetEpisodesByStationAndSeriesID2(xs:string authTicket, xs:string uniqueChannelID, xs:string epgID, )
            GetEpisodesByStationAndTimeRanges(xs:string authTicket, xs:string uniqueChannelIDStart, xs:string uniqueChannelIDEnd, xs:unsignedLong timeStart, xs:unsignedLong timeEnd, )
            GetFirstEpisodeBySeriesID(xs:string authTicket, xs:string epgID, )
            GetFirstEpisodeByStationAndSeriesID2(xs:string authTicket, xs:string uniqueChannelID, xs:string epgID, )
            GetLastUpdateTime(xs:string authTicket, )
            GetSeries2(xs:string authTicket, xs:unsignedInt uiStart, xs:unsignedInt uiResults, )
            GetSeriesByCategory(xs:string authTicket, xs:string bstrCategory, xs:string bstrSubcategory, )
            GetSeriesByKeyword(xs:string authTicket, xs:string keyword, )
            GetSeriesByKeywordWithLimit(xs:string authTicket, xs:string keyword, xs:unsignedInt limit, )
            GetSeriesByRange(xs:string authTicket, xs:unsignedInt uiStart, xs:unsignedInt uiEnd, )
            GetSeriesCollection(xs:string authTicket, )
            GetSeriesCount(xs:string authTicket, )
            GetSeriesCount2(xs:string authTicket, )
            SearchByTitle(xs:string authTicket, xs:string partialTitle, )
            SearchByTitle2(xs:string authTicket, xs:string partialTitle, )
         Types (6):
            ArrayOfArrayOfString
            ns1:ArrayOfPVSProperty
            ArrayOfPVSPropertyBag
            ArrayOfString
            ns1:PVSProperty
            ns1:PVSPropertyBag
      (BTVGuideDataSoap12)
         Methods (26):
            GetCategories(xs:string authTicket, xs:string bstrCategory, )
            GetDataExtents(xs:string authTicket, )
            GetEpisodeByStationAndTime(xs:string authTicket, xs:string uniqueChannelID, xs:unsignedLong time, )
            GetEpisodesByKeyword(xs:string authTicket, xs:string keyword, )
            GetEpisodesByKeywordWithLimit(xs:string authTicket, xs:string keyword, xs:unsignedInt limit, )
            GetEpisodesByKeywordWithOptions(xs:string authTicket, xs:string keyword, xs:int titleSearch, xs:int descSearch, xs:int actorSearch, xs:unsignedInt limit, )
            GetEpisodesByRange2(xs:string authTicket, xs:string uniqueChannelIDStart, xs:string uniqueChannelIDEnd, xs:unsignedLong timeStart, xs:unsignedLong timeEnd, )
            GetEpisodesByRange3(xs:string authTicket, ArrayOfString channels, xs:unsignedLong timeStart, xs:unsignedLong timeEnd, )
            GetEpisodesBySeriesID(xs:string authTicket, xs:string epgID, )
            GetEpisodesByStation(xs:string authTicket, xs:string uniqueChannelID, )
            GetEpisodesByStationAndSeriesID(xs:string authTicket, xs:string uniqueChannelID, xs:string epgID, xs:unsignedLong time, )
            GetEpisodesByStationAndSeriesID2(xs:string authTicket, xs:string uniqueChannelID, xs:string epgID, )
            GetEpisodesByStationAndTimeRanges(xs:string authTicket, xs:string uniqueChannelIDStart, xs:string uniqueChannelIDEnd, xs:unsignedLong timeStart, xs:unsignedLong timeEnd, )
            GetFirstEpisodeBySeriesID(xs:string authTicket, xs:string epgID, )
            GetFirstEpisodeByStationAndSeriesID2(xs:string authTicket, xs:string uniqueChannelID, xs:string epgID, )
            GetLastUpdateTime(xs:string authTicket, )
            GetSeries2(xs:string authTicket, xs:unsignedInt uiStart, xs:unsignedInt uiResults, )
            GetSeriesByCategory(xs:string authTicket, xs:string bstrCategory, xs:string bstrSubcategory, )
            GetSeriesByKeyword(xs:string authTicket, xs:string keyword, )
            GetSeriesByKeywordWithLimit(xs:string authTicket, xs:string keyword, xs:unsignedInt limit, )
            GetSeriesByRange(xs:string authTicket, xs:unsignedInt uiStart, xs:unsignedInt uiEnd, )
            GetSeriesCollection(xs:string authTicket, )
            GetSeriesCount(xs:string authTicket, )
            GetSeriesCount2(xs:string authTicket, )
            SearchByTitle(xs:string authTicket, xs:string partialTitle, )
            SearchByTitle2(xs:string authTicket, xs:string partialTitle, )
         Types (6):
            ArrayOfArrayOfString
            ns1:ArrayOfPVSProperty
            ArrayOfPVSPropertyBag
            ArrayOfString
            ns1:PVSProperty
            ns1:PVSPropertyBag




-=-=-=-=-=   API for BTVLog

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( BTVLog ) tns="http://www.snapstream.com/WebService"
   Prefixes (0)
   Ports (2):
      (BTVLogSoap)
         Methods (12):
            DismissAllErrors(xs:string authTicket, )
            DismissError(xs:string authTicket, xs:unsignedLong timestamp, xs:unsignedInt errorCode, )
            GetNextError(xs:string authTicket, xs:unsignedLong startTime, )
            GetNextErrorWithDescription(xs:string authTicket, xs:unsignedLong startTime, )
            GetNextMessage(xs:string authTicket, xs:unsignedLong pqwStart, )
            IgnoreError(xs:string authTicket, xs:unsignedInt errorCode, )
            LogError(xs:string authTicket, xs:unsignedInt ulErrorCode, xs:int bUnique, xs:int bUniqueDesc, xs:string bstrErrStr, )
            ReactivateIgnoredErrors(xs:string authTicket, )
            WriteChange(xs:string authTicket, xs:string bstrSetting, xs:string bstrOldValue, xs:string bstrNewValue, )
            WriteLog(xs:string authTicket, xs:string bstrMsg, )
            WriteLogMessagesToDisk(xs:string authTicket, )
            WriteLogUser(xs:string authTicket, xs:string bstrMsg, )
         Types (0):
      (BTVLogSoap12)
         Methods (12):
            DismissAllErrors(xs:string authTicket, )
            DismissError(xs:string authTicket, xs:unsignedLong timestamp, xs:unsignedInt errorCode, )
            GetNextError(xs:string authTicket, xs:unsignedLong startTime, )
            GetNextErrorWithDescription(xs:string authTicket, xs:unsignedLong startTime, )
            GetNextMessage(xs:string authTicket, xs:unsignedLong pqwStart, )
            IgnoreError(xs:string authTicket, xs:unsignedInt errorCode, )
            LogError(xs:string authTicket, xs:unsignedInt ulErrorCode, xs:int bUnique, xs:int bUniqueDesc, xs:string bstrErrStr, )
            ReactivateIgnoredErrors(xs:string authTicket, )
            WriteChange(xs:string authTicket, xs:string bstrSetting, xs:string bstrOldValue, xs:string bstrNewValue, )
            WriteLog(xs:string authTicket, xs:string bstrMsg, )
            WriteLogMessagesToDisk(xs:string authTicket, )
            WriteLogUser(xs:string authTicket, xs:string bstrMsg, )
         Types (0):




-=-=-=-=-=   API for InternalBTVScheduler

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( InternalBTVScheduler ) tns="http://www.snapstream.com/WebService"
   Prefixes (0)
   Ports (2):
      (InternalBTVSchedulerSoap)
         Methods (3):
            AddNotificationRecordings(xs:long l, xs:string s, xs:string episodes, )
            GetEpisodesByKeywordWithLimit(xs:long l, xs:string s, xs:string keyword, xs:unsignedInt limit, )
            GetLastIndexChanged(xs:long l, xs:string s, )
         Types (0):
      (InternalBTVSchedulerSoap12)
         Methods (3):
            AddNotificationRecordings(xs:long l, xs:string s, xs:string episodes, )
            GetEpisodesByKeywordWithLimit(xs:long l, xs:string s, xs:string keyword, xs:unsignedInt limit, )
            GetLastIndexChanged(xs:long l, xs:string s, )
         Types (0):




-=-=-=-=-=   API for BTVGuideUpdater

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( BTVGuideUpdater ) tns="http://www.snapstream.com/WebService"
   Prefixes (0)
   Ports (2):
      (BTVGuideUpdaterSoap)
         Methods (9):
            CancelUpdate(xs:string authTicket, )
            GetLastAttemptedUpdate(xs:string authTicket, )
            GetLastSuccessfulUpdate(xs:string authTicket, )
            GetNextAttemptedUpdate(xs:string authTicket, )
            GetProgress(xs:string authTicket, )
            GetRemoteRecordings(xs:string authTicket, )
            SetProperty(xs:string authTicket, xs:string name, xs:string val, )
            StartUpdate(xs:string authTicket, )
            TVTVUpdate(xs:string authTicket, )
         Types (0):
      (BTVGuideUpdaterSoap12)
         Methods (9):
            CancelUpdate(xs:string authTicket, )
            GetLastAttemptedUpdate(xs:string authTicket, )
            GetLastSuccessfulUpdate(xs:string authTicket, )
            GetNextAttemptedUpdate(xs:string authTicket, )
            GetProgress(xs:string authTicket, )
            GetRemoteRecordings(xs:string authTicket, )
            SetProperty(xs:string authTicket, xs:string name, xs:string val, )
            StartUpdate(xs:string authTicket, )
            TVTVUpdate(xs:string authTicket, )
         Types (0):




-=-=-=-=-=   API for BTVNotifier

Suds ( https://fedorahosted.org/suds/ )  version: 0.3.4 (GA)  build: R457-20090224

Service ( BTVNotifier ) tns="http://www.snapstream.com/WebService"
   Prefixes (2)
      ns0 = "http://microsoft.com/wsdl/types/"
      ns1 = "http://www.snapstream.com/WebService"
   Ports (2):
      (BTVNotifierSoap)
         Methods (10):
            AddGuideSearch(xs:string authTicket, xs:string prop, xs:int sendTestEmail, )
            AddNotification(xs:string authTicket, xs:string prop, xs:int sendTestEmail, )
            EditGuideSearch(xs:string authTicket, xs:string prop, xs:int sendTestEmail, )
            EditNotification(xs:string authTicket, xs:string prop, xs:int sendTestEmail, )
            GetNotification(xs:string authTicket, ns0:guid guid, )
            GetNotifications(xs:string authTicket, )
            GetSavedGuideSearch(xs:string authTicket, ns0:guid guid, )
            GetSavedGuideSearches(xs:string authTicket, )
            RemoveGuideSearch(xs:string authTicket, ns0:guid guid, )
            RemoveNotification(xs:string authTicket, ns0:guid guid, )
         Types (1):
            ns0:guid
      (BTVNotifierSoap12)
         Methods (10):
            AddGuideSearch(xs:string authTicket, xs:string prop, xs:int sendTestEmail, )
            AddNotification(xs:string authTicket, xs:string prop, xs:int sendTestEmail, )
            EditGuideSearch(xs:string authTicket, xs:string prop, xs:int sendTestEmail, )
            EditNotification(xs:string authTicket, xs:string prop, xs:int sendTestEmail, )
            GetNotification(xs:string authTicket, ns0:guid guid, )
            GetNotifications(xs:string authTicket, )
            GetSavedGuideSearch(xs:string authTicket, ns0:guid guid, )
            GetSavedGuideSearches(xs:string authTicket, )
            RemoveGuideSearch(xs:string authTicket, ns0:guid guid, )
            RemoveNotification(xs:string authTicket, ns0:guid guid, )
         Types (1):
            ns0:guid



