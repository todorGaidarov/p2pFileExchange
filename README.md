P2P file transfer similar 

The project consists of two parts:
- server which hold the metadata for the files shared by the peers.
  The metadata is - name, ralativePath, size and list of addresses
  of the peers that share the file.

- client which can register/unregister files in the metadata server
  and this way tells that the file can be downloaded from it.
  
 
 TODO: 
 - improve error handling by adding custom exception
 - implement file transfer - downloading from single or many peers
