package com.hayukleung.designpattern.Mediator;

public class MainBoard implements Mediator {
    private CDDriver cdDriver = null;
    private CPU cpu = null;
    private VideoCard videoCard = null;
    private SoundCard soundCard = null;

    public void setCdDriver(CDDriver cdDriver) {
        this.cdDriver = cdDriver;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    public void setVideoCard(VideoCard videoCard) {
        this.videoCard = videoCard;
    }

    public void setSoundCard(SoundCard soundCard) {
        this.soundCard = soundCard;
    }

    @Override
    public void changed(Colleague c) {
        if (c instanceof CDDriver) {
            this.opeCDDriverReadData((CDDriver) c);
        } else if (c instanceof CPU) {
            this.opeCPU((CPU) c);
        }
    }

    private void opeCDDriverReadData(CDDriver cd) {
        String data = cd.getData();
        cpu.executeData(data);
    }

    private void opeCPU(CPU cpu) {
        String videoData = cpu.getVideoData();
        String soundData = cpu.getSoundData();
        videoCard.showData(videoData);
        soundCard.soundData(soundData);
    }
}