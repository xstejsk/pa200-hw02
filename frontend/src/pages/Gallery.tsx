import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Table, Sheet, Typography, IconButton, Button, Box, Input } from "@mui/joy";
import Download from "@mui/icons-material/Download";
import Upload from "@mui/icons-material/Upload";
import photosApi from "../service/photosApi.ts";

export default function Gallery() {
  const queryClient = useQueryClient();

  const { data: photos = [], isLoading, isError } = useQuery({
    queryKey: ["photos"],
    queryFn: () => photosApi.getAll(),
  });

  const [page, setPage] = useState(1);
  const rowsPerPage = 5;
  const totalPages = Math.ceil(photos.length / rowsPerPage);
  const [downloadingFiles, setDownloadingFiles] = useState<string[]>([]);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  const uploadMutation = useMutation({
    mutationFn: (file: File) => {
      const formData = new FormData();
      formData.append("file", file);
      return photosApi.create(formData);
    },
    onSuccess: () => {
      queryClient.invalidateQueries(["photos"]);
    },
    onError: (error: any) => {
      if (error.response?.status === 409) {
        alert("A file with this name already exists.");
      } else {
        console.error("Upload failed:", error);
      }
      queryClient.invalidateQueries(["photos"]);
    },
  });

  if (isError) return <Typography color="danger">Error loading photos</Typography>;
  if (isLoading) return <Typography>Loading...</Typography>;

  const handleDownload = async (blobName: string) => {
    try {
      setDownloadingFiles(prev => [...prev, blobName]);
      const sasUrl = await photosApi.getPhotoSasUrl(blobName);
      const link = document.createElement("a");
      link.href = sasUrl;
      link.download = blobName;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (error) {
      console.error("Failed to fetch SAS URL:", error);
    } finally {
      setDownloadingFiles(prev => prev.filter(name => name !== blobName));
    }
  };

  const handleUpload = () => {
    if (selectedFile) {
      uploadMutation.mutate(selectedFile);
      setSelectedFile(null); // Reset after upload
    }
  };

  return (
      <Box className="main" sx={{ display: "flex", justifyContent: "center", mt: 2, height: "fit-content" }}>
        <Sheet variant="outlined" sx={{ p: 2, borderRadius: "sm" }}>
          <Typography level="h4" sx={{ mb: 2 }}>Super Secret Storage</Typography>

          {/* Upload File Input */}
          <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
            <Input
                type="file"
                onChange={(e) => setSelectedFile(e.target.files?.[0] || null)}
            />
            <Button
                onClick={handleUpload}
                disabled={!selectedFile || uploadMutation.isLoading}
                startDecorator={<Upload />}
            >
              Upload
            </Button>
          </Box>

          {/* Gallery Table */}
          <Table aria-label="Photo Gallery">
            <thead>
            <tr>
              <th>File Name</th>
              <th>Created At</th>
              <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            {photos.slice((page - 1) * rowsPerPage, page * rowsPerPage).map((photo) => (
                <tr key={photo.blobName}>
                  <td>{photo.fileName}</td>
                  <td>{new Date(photo.createdAt).toLocaleString()}</td>
                  <td>
                    {downloadingFiles.includes(photo.blobName) ? (
                        <Typography>Downloading...</Typography>
                    ) : (
                        <IconButton onClick={() => handleDownload(photo.blobName)} color="primary">
                          <Download />
                        </IconButton>
                    )}
                  </td>
                </tr>
            ))}
            </tbody>
          </Table>

          {/* Pagination Controls */}
          <Sheet sx={{ display: "flex", justifyContent: "center", mt: 2, gap: 1 }}>
            <Button
                variant="outlined"
                onClick={() => setPage((prev) => Math.max(prev - 1, 1))}
                disabled={page === 1}
            >
              Previous
            </Button>
            <Typography level="body-md">Page {page} of {totalPages}</Typography>
            <Button
                variant="outlined"
                onClick={() => setPage((prev) => Math.min(prev + 1, totalPages))}
                disabled={page === totalPages}
            >
              Next
            </Button>
          </Sheet>
        </Sheet>
      </Box>
  );
}
